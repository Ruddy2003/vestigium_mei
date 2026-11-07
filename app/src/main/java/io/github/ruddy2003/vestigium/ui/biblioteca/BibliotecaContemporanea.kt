package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.hypot
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
 * La tapa es ese quaderno tumbado y grande, con los boletos asomando, «QUADERNO» en su franja, la etiqueta blanca con la
 * fecha y las parole, y la liga que se suelta al abrirlo.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.18f, 0.415f, 0.76f, 0.79f)
    override val letraTapa: FontFamily = Letra.cronaca
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 parola" else "$n parole"

    /** El papel de la etiqueta de la tapa: hundido en oscuro y apenas azulado de noche, pero siempre claro para el extracto. */
    private fun papelTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, Color(0xFF100E0C), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.12f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val tapa = c(TAPAS[(e.semilla and 0x7fffffff) % TAPAS.size])
        val papel = papelTapa(oscuro, e.noche)
        val cu = Rect(w * 0.03f, h * 0.1f, w * 0.95f, h * 0.95f)
        val bisagra = cu.left + w * 0.06f
        // La sombra en la mesa y el canto de las hojas, que asoma abajo y a la derecha.
        drawRoundRect(Color.Black.copy(alpha = 0.18f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(6 * u))
        drawRoundRect(c(PAPEL), cu.topLeft + Offset(2.5f * u, 2.5f * u), cu.size, CornerRadius(6 * u))
        for (k in 1..3) {
            drawLine(c(TINTA).copy(alpha = 0.14f), Offset(cu.left + 8 * u, cu.bottom + k * 0.7f * u), Offset(cu.right + 1 * u, cu.bottom + k * 0.7f * u), 0.5f * u)
            drawLine(c(TINTA).copy(alpha = 0.14f), Offset(cu.right + k * 0.7f * u, cu.top + 6 * u), Offset(cu.right + k * 0.7f * u, cu.bottom + 1 * u), 0.5f * u)
        }
        // Los boletos de autobús guardados entre las hojas, que asoman por arriba: más cuanto más escribiste.
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val boletos = 1 + (lleno * 3).toInt()
        for (j in 0 until boletos) {
            val bw = w * 0.085f; val bh = h * 0.22f
            val bx = w * (0.2f + 0.13f * j) + w * 0.04f * azar(e.semilla, 20 + j)
            val sale = h * (0.045f + 0.04f * azar(e.semilla, 30 + j))
            val o = Offset(bx, cu.top - sale)
            rotate(-9f + 18f * azar(e.semilla, 40 + j), Offset(o.x + bw / 2, cu.top)) {
                drawRect(c(BOLETOS[(j + (e.semilla and 3)) % BOLETOS.size]), o, Size(bw, bh))
                drawRect(c(TINTA).copy(alpha = 0.22f), Offset(o.x, o.y + bh * 0.08f), Size(bw, bh * 0.05f))
                drawLine(c(TINTA).copy(alpha = 0.4f), Offset(o.x + bw * 0.16f, o.y + bh * 0.22f), Offset(o.x + bw * 0.62f, o.y + bh * 0.22f), 0.7f * u)
                drawLine(c(TINTA).copy(alpha = 0.3f), Offset(o.x + bw * 0.16f, o.y + bh * 0.3f), Offset(o.x + bw * 0.45f, o.y + bh * 0.3f), 0.6f * u)
                // El timbre de la validadora, torcido.
                drawRect(c(ROJO).copy(alpha = 0.45f), Offset(o.x + bw * 0.62f, o.y + bh * 0.26f), Size(bw * 0.28f, bh * 0.07f), style = Stroke(0.6f * u))
            }
        }
        // Por dentro (se ve al abrirse): la hoja de cuadritos con su margen rojo.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(PAPEL), hoja.topLeft, hoja.size)
            var gx = hoja.left + 5 * u
            while (gx < hoja.right) { drawLine(c(VIDRIO).copy(alpha = 0.5f), Offset(gx, hoja.top), Offset(gx, hoja.bottom), 0.5f * u); gx += 5 * u }
            var gy = hoja.top + 5 * u
            while (gy < hoja.bottom) { drawLine(c(VIDRIO).copy(alpha = 0.5f), Offset(hoja.left, gy), Offset(hoja.right, gy), 0.5f * u); gy += 5 * u }
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(hoja.left + 20 * u, hoja.top), Offset(hoja.left + 20 * u, hoja.bottom), 0.8f * u)
        }
        // La tapa, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(tapa, cu.topLeft, cu.size, CornerRadius(6 * u))
            var y = cu.top + 2 * u
            while (y < cu.bottom) { drawLine(Color.Black.copy(alpha = 0.04f), Offset(cu.left, y), Offset(cu.right, y), 0.6f * u); y += 3 * u }
            // De noche, la luz de los faroles entra por las lamas de la persiana y raya la tapa.
            if (e.noche) clipRect(cu.left, cu.top, cu.right, cu.bottom) {
                rotate(-20f, cu.center) {
                    for (k in 0 until 5) drawRect(FAROL.copy(alpha = 0.07f), Offset(cu.left + cu.width * (0.08f + 0.2f * k), cu.top - cu.height), Size(cu.width * 0.06f, cu.height * 3))
                }
            }
            // La franja de arriba con «QUADERNO» y el sello en seco; de noche, una luna.
            val franja = Rect(bisagra, cu.top + h * 0.035f, cu.right, cu.top + h * 0.14f)
            drawRect(Color.Black.copy(alpha = 0.2f), franja.topLeft, franja.size)
            drawLine(Color.White.copy(alpha = 0.14f), Offset(franja.left, franja.bottom), Offset(franja.right, franja.bottom), 0.7f * u)
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(franja.left, franja.top), Offset(franja.right, franja.top), 0.7f * u)
            textoEn(medidor, "QUADERNO", Rect(franja.left + 8 * u, franja.top, franja.left + w * 0.34f, franja.bottom), letra, c(PAPEL).copy(alpha = 0.92f), 11f,
                peso = FontWeight.Bold, espaciado = 2.5f)
            val cs = Offset(w * 0.63f, franja.center.y)
            val s = franja.height * 0.36f
            if (e.noche) {
                drawCircle(LUNA, s * 0.9f, cs)
                drawCircle(Color.Black.copy(alpha = 0.2f), s * 0.78f, Offset(cs.x + s * 0.42f, cs.y - s * 0.22f))
                drawCircle(tapa, s * 0.78f, Offset(cs.x + s * 0.42f, cs.y - s * 0.22f))
                listOf(Offset(-1.8f, 0.6f), Offset(1.9f, 0.7f), Offset(2.6f, -0.5f)).forEach { k -> drawCircle(LUNA.copy(alpha = 0.8f), 0.9f * u, Offset(cs.x + s * k.x, cs.y + s * k.y)) }
            } else {
                // El relieve del sello: la luz arriba, la sombra abajo.
                val sello = Rect(cs.x - s * 1.4f, cs.y - s * 0.8f, cs.x + s * 1.4f, cs.y + s * 0.8f)
                drawRoundRect(Color.Black.copy(alpha = 0.22f), sello.topLeft + Offset(0.8f * u, 0.8f * u), sello.size, CornerRadius(s * 0.8f), style = Stroke(1 * u))
                drawRoundRect(Color.White.copy(alpha = 0.16f), sello.topLeft, sello.size, CornerRadius(s * 0.8f), style = Stroke(1 * u))
                drawLine(Color.White.copy(alpha = 0.16f), Offset(sello.left + s * 0.6f, cs.y), Offset(sello.right - s * 0.6f, cs.y), 0.9f * u)
                drawCircle(Color.White.copy(alpha = 0.16f), s * 0.22f, Offset(sello.left + s * 0.35f, cs.y))
                drawCircle(Color.White.copy(alpha = 0.16f), s * 0.22f, Offset(sello.right - s * 0.35f, cs.y))
            }
            // La etiqueta blanca con su marco de color: la fecha arriba, el extracto en medio y las parole al pie.
            val et = Rect(w * 0.15f, h * 0.285f, w * 0.79f, h * 0.905f)
            drawRoundRect(Color.Black.copy(alpha = 0.22f), et.topLeft + Offset(1.2f * u, 1.8f * u), et.size, CornerRadius(7 * u))
            drawRoundRect(papel, et.topLeft, et.size, CornerRadius(7 * u))
            drawRoundRect(c(ROJO).copy(alpha = 0.75f), et.deflate(3.5f * u).topLeft, et.deflate(3.5f * u).size, CornerRadius(5 * u), style = Stroke(1.1f * u))
            textoEn(medidor, e.fecha, Rect(et.left + 9 * u, h * 0.312f, et.right - 9 * u, h * 0.385f), letra, c(ROJO), 11f, peso = FontWeight.Bold, minimo = 6.5f)
            drawLine(c(ROJO).copy(alpha = 0.7f), Offset(et.left + 9 * u, h * 0.397f), Offset(et.right - 9 * u, h * 0.397f), 0.8f * u)
            drawLine(c(TINTA).copy(alpha = 0.25f), Offset(et.left + 9 * u, h * 0.805f), Offset(et.right - 9 * u, h * 0.805f), 0.6f * u)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(et.left + 9 * u, h * 0.815f, et.right - 9 * u, h * 0.882f), letra, c(TINTA).copy(alpha = 0.7f), 9f, minimo = 6f)
            }
            // Las esquinas de la tapa, un poco gastadas.
            listOf(Offset(cu.right, cu.top) to -1f, Offset(cu.right, cu.bottom) to 1f).forEach { (o, s0) ->
                val desgaste = Path().apply { moveTo(o.x, o.y + s0 * 9 * u); lineTo(o.x, o.y); lineTo(o.x - 9 * u, o.y); close() }
                drawPath(desgaste, Color.White.copy(alpha = 0.08f))
            }
            // Al girar, la tapa se ve de canto y se oscurece.
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(6 * u))
        }
        // La sombra que la tapa echa sobre la hoja mientras gira.
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            val ancho = 14 * u * kotlin.math.sin(Math.PI.toFloat() * a)
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(borde, cu.top), Size(ancho, cu.height))
        }
        // El lomo de tela, a la izquierda, con su trama y su brillo.
        drawRoundRect(tapa, cu.topLeft, Size(bisagra - cu.left + 2 * u, cu.height), CornerRadius(6 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), cu.topLeft, Size(bisagra - cu.left, cu.height))
        var lx = cu.left + 1.5f * u
        while (lx < bisagra) { drawLine(Color.White.copy(alpha = 0.05f), Offset(lx, cu.top), Offset(lx, cu.bottom), 0.5f * u); lx += 1.8f * u }
        drawLine(Color.White.copy(alpha = 0.16f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 0.8f * u)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(bisagra + 1 * u, cu.top), Offset(bisagra + 1 * u, cu.bottom), 0.6f * u)
        // La liga elástica que lo cierra: al abrirse, se suelta hacia la derecha.
        val liga = (1f - a * 2.2f).coerceAtLeast(0f)
        if (liga > 0f) {
            val xl = w * 0.865f + a * w * 0.12f
            drawRect(Color.Black.copy(alpha = 0.25f * liga), Offset(xl + 1.4f * u, cu.top), Size(w * 0.028f, cu.height))
            drawRect(c(Color(0xFF17181A)).copy(alpha = liga), Offset(xl, cu.top - 1.5f * u), Size(w * 0.028f, cu.height + 3 * u))
            drawLine(Color.White.copy(alpha = 0.2f * liga), Offset(xl + w * 0.007f, cu.top), Offset(xl + w * 0.007f, cu.bottom), 0.7f * u)
            var ty = cu.top + 2 * u
            while (ty < cu.bottom) { drawLine(Color.White.copy(alpha = 0.05f * liga), Offset(xl, ty), Offset(xl + w * 0.028f, ty), 0.5f * u); ty += 2 * u }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    /** El café de la moka, para el cerco que dejó la taza en la hoja. */
    private val CAFE = Color(0xFF6B4423)

    override val altoEncabezado: Dp = 122.dp

    /**
     * La cabecera de la crónica del barrio, entre una raya gruesa y una fina: a la izquierda la tapa del tombino con su
     * cuadrícula, al centro «CRONACA DI ROMA» con el rione del día y a la derecha el boleto picado con el número del día.
     * Debajo, la fecha entre las dos orejas del periódico (el sol y la tazzina) y el detalle. De noche, la luna y sus
     * estrellas en lugar del tombino, y en las orejas el farol y las estrellas.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val raya = if (e.noche) p.acento else p.tinta
        drawRect(raya, Offset(0f, 1 * u), Size(w, 2.2f * u))
        drawRect(raya, Offset(0f, 5 * u), Size(w, 0.8f * u))
        // A la izquierda, la tapa del tombino; de noche, la luna con sus estrellas.
        val ct = Offset(15 * u, 22 * u)
        if (e.noche) {
            crecienteCon(ct, 9 * u, p.acento)
            listOf(Offset(9f, -8f), Offset(12f, 5f), Offset(-8f, 9f)).forEach { k ->
                drawPath(estrella(Offset(ct.x + k.x * u, ct.y + k.y * u), 2.4f * u, 4, 0.35f), p.acento.copy(alpha = 0.8f))
            }
        } else {
            drawCircle(Color.Black.copy(alpha = 0.15f), 11 * u, ct + Offset(0.6f * u, 1 * u))
            drawCircle(c(HIERRO), 11 * u, ct)
            drawCircle(c(HIERRO_LUZ), 10.4f * u, ct, style = Stroke(1.1f * u))
            clipPath(Path().apply { addOval(Rect(ct, 8.4f * u)) }) {
                var k = -8f
                while (k <= 8f) {
                    drawLine(c(HIERRO_LUZ).copy(alpha = 0.6f), Offset(ct.x + k * u, ct.y - 9 * u), Offset(ct.x + k * u, ct.y + 9 * u), 0.5f * u)
                    drawLine(c(HIERRO_LUZ).copy(alpha = 0.6f), Offset(ct.x - 9 * u, ct.y + k * u), Offset(ct.x + 9 * u, ct.y + k * u), 0.5f * u)
                    k += 2.8f
                }
            }
            drawRect(c(HIERRO), Offset(ct.x - 7.5f * u, ct.y - 3 * u), Size(15 * u, 6 * u))
            textoEn(medidor, "SPQR", Rect(ct.x - 7 * u, ct.y - 3 * u, ct.x + 7 * u, ct.y + 3 * u), letra, c(TRAVERTINO), 5.5f,
                peso = FontWeight.Bold, minimo = 3.5f)
        }
        // A la derecha, el boleto del autobús con el número del día, torcido, con el timbre de la validadora.
        val bo = Rect(w - 50 * u, 11 * u, w - 3 * u, 33 * u)
        rotate(-4f, bo.center) {
            drawRect(Color.Black.copy(alpha = 0.16f), bo.topLeft + Offset(1 * u, 1.2f * u), bo.size)
            drawRect(c(BOLETOS[0]), bo.topLeft, bo.size)
            drawRect(c(TINTA).copy(alpha = 0.28f), Offset(bo.left, bo.top + 3 * u), Size(bo.width, 2.4f * u))
            textoEn(medidor, "Nº ${e.numero}", Rect(bo.left + 3 * u, bo.top + 7 * u, bo.right - 13 * u, bo.bottom - 1.5f * u), letra, c(TINTA), 9f,
                peso = FontWeight.Bold, minimo = 5f)
            drawRect(c(ROJO).copy(alpha = 0.65f), Offset(bo.right - 11.5f * u, bo.top + 8 * u), Size(8.5f * u, 10.5f * u), style = Stroke(0.8f * u))
            drawLine(c(ROJO).copy(alpha = 0.55f), Offset(bo.right - 10 * u, bo.top + 11.5f * u), Offset(bo.right - 4.5f * u, bo.top + 11.5f * u), 0.6f * u)
            drawLine(c(ROJO).copy(alpha = 0.55f), Offset(bo.right - 10 * u, bo.top + 14.5f * u), Offset(bo.right - 6 * u, bo.top + 14.5f * u), 0.6f * u)
        }
        // El nombre de la crónica con el rione del día.
        textoEn(medidor, e.rotulo, Rect(31 * u, 9 * u, w - 55 * u, 36 * u), Letra.placa, p.acento, 12f, peso = FontWeight.Bold, espaciado = 1f, minimo = 6.5f)
        drawRect(raya, Offset(0f, 39 * u), Size(w, 0.8f * u))
        // Las orejas: el tiempo a la izquierda (el sol; de noche, el farol) y la tazzina a la derecha (de noche, estrellas).
        val izq = Rect(0f, 46 * u, 28 * u, 86 * u)
        val der = Rect(w - 28 * u, 46 * u, w, 86 * u)
        listOf(izq, der).forEach { drawRect(raya.copy(alpha = 0.45f), it.topLeft, it.size, style = Stroke(0.7f * u)) }
        val trazo = (if (e.noche) p.acento else p.tinta).copy(alpha = 0.75f)
        if (e.noche) {
            val f = Offset(izq.center.x, izq.top + 9 * u)
            drawCircle(c(FAROL).copy(alpha = 0.18f), 8 * u, f)
            drawCircle(c(FAROL).copy(alpha = 0.3f), 4.5f * u, f)
            drawPath(Path().apply { moveTo(f.x - 3.5f * u, f.y - 3 * u); lineTo(f.x + 3.5f * u, f.y - 3 * u); lineTo(f.x + 2.2f * u, f.y + 3 * u); lineTo(f.x - 2.2f * u, f.y + 3 * u); close() }, c(FAROL))
            drawLine(trazo, Offset(f.x, f.y + 3 * u), Offset(f.x, izq.bottom - 5 * u), 1.2f * u)
            drawLine(trazo, Offset(f.x - 4 * u, izq.bottom - 5 * u), Offset(f.x + 4 * u, izq.bottom - 5 * u), 1.2f * u)
            listOf(Offset(-6f, -9f), Offset(5f, -3f), Offset(-3f, 6f), Offset(7f, 9f)).forEach { k ->
                drawPath(estrella(Offset(der.center.x + k.x * u, der.center.y + k.y * u), 2.2f * u, 4, 0.35f), p.acento.copy(alpha = 0.8f))
            }
        } else {
            val s = izq.center
            drawCircle(trazo, 4.5f * u, s, style = Stroke(1 * u))
            for (k in 0 until 8) {
                val a = Math.toRadians(45.0 * k)
                drawLine(trazo, Offset(s.x + 6.5f * u * cos(a).toFloat(), s.y + 6.5f * u * sin(a).toFloat()),
                    Offset(s.x + 9.5f * u * cos(a).toFloat(), s.y + 9.5f * u * sin(a).toFloat()), 0.9f * u)
            }
            // La tazzina en su plato, con el vapor.
            val tz = Offset(der.center.x, der.center.y + 4 * u)
            drawPath(Path().apply { moveTo(tz.x - 6 * u, tz.y - 4 * u); lineTo(tz.x + 6 * u, tz.y - 4 * u); lineTo(tz.x + 4.5f * u, tz.y + 3 * u); lineTo(tz.x - 4.5f * u, tz.y + 3 * u); close() }, trazo)
            drawArc(trazo, -70f, 160f, false, Offset(tz.x + 4 * u, tz.y - 3 * u), Size(5 * u, 5 * u), style = Stroke(1 * u))
            drawLine(trazo, Offset(tz.x - 8 * u, tz.y + 4.5f * u), Offset(tz.x + 8 * u, tz.y + 4.5f * u), 1.2f * u)
            listOf(-2.5f, 2f).forEach { dx ->
                drawPath(Path().apply { moveTo(tz.x + dx * u, tz.y - 6 * u); quadraticTo(tz.x + (dx - 2.5f) * u, tz.y - 9 * u, tz.x + dx * u, tz.y - 12 * u)
                    quadraticTo(tz.x + (dx + 2.5f) * u, tz.y - 15 * u, tz.x + dx * u, tz.y - 18 * u) }, trazo.copy(alpha = 0.45f), style = Stroke(0.8f * u))
            }
        }
        // La fecha, entre las orejas, y el detalle debajo.
        fechaCon(medidor, e.fecha, Rect(33 * u, 44 * u, w - 33 * u, 88 * u), Letra.cronaca, p.tinta, 23f)
        textoEn(medidor, e.detalle, Rect(6 * u, 90 * u, w - 6 * u, 106 * u), Letra.cronaca, p.tenue, 11.5f, minimo = 7f)
        drawRect(raya, Offset(0f, 112 * u), Size(w, 0.8f * u))
        drawRect(raya, Offset(0f, 115 * u), Size(w, 2.2f * u))
    }

    /**
     * La hoja del quaderno: el margen a lápiz con su raya roja, el boleto pegado con dos tiras de cinta que asoma por la derecha,
     * el cerco que dejó la tazzina de la moka abajo a la izquierda y el matasellos redondo con sus ondas abajo a la derecha.
     * En modo oscuro, el farol de la calle entra por arriba a la derecha y raya la hoja con las lamas de la persiana.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        drawLine(p.tenue.copy(alpha = 0.32f), Offset(21 * u, 0f), Offset(21 * u, h), 1 * u)
        drawLine(p.acento.copy(alpha = 0.24f), Offset(24 * u, 0f), Offset(24 * u, h), 0.8f * u)
        // El boleto pegado con dos tiras de cinta, que asoma por la orilla derecha como un señalador.
        val o = Offset(w - 44 * u, h * 0.6f)
        rotate(-84f, o + Offset(35 * u, 14 * u)) {
            drawRect(Color.Black.copy(alpha = 0.08f), o + Offset(1.5f * u, 2 * u), Size(70 * u, 28 * u))
            drawRect(c(BOLETOS[3]).copy(alpha = 0.85f), o, Size(70 * u, 28 * u))
            drawRect(p.detalle.copy(alpha = 0.45f), o + Offset(0f, 4 * u), Size(70 * u, 4 * u))
            drawLine(c(TINTA).copy(alpha = 0.35f), o + Offset(6 * u, 13 * u), o + Offset(40 * u, 13 * u), 0.7f * u)
            drawLine(c(TINTA).copy(alpha = 0.25f), o + Offset(6 * u, 18 * u), o + Offset(30 * u, 18 * u), 0.6f * u)
            drawRect(c(ROJO).copy(alpha = 0.4f), o + Offset(50 * u, 11 * u), Size(12 * u, 13 * u), style = Stroke(0.8f * u))
            listOf(Offset(-6f, -4f), Offset(60f, 22f)).forEach { d ->
                drawRect(Color(0xFFF2EAC6).copy(alpha = 0.55f), o + Offset(d.x * u, d.y * u), Size(17 * u, 7 * u))
            }
        }
        // El cerco de la tazzina: dos anillos de café incompletos.
        val cc = Offset(64 * u, h - 82 * u)
        drawCircle(CAFE.copy(alpha = 0.035f), 27 * u, cc)
        drawArc(CAFE.copy(alpha = 0.14f), 25f, 285f, false, Offset(cc.x - 28 * u, cc.y - 28 * u), Size(56 * u, 56 * u), style = Stroke(2.6f * u))
        drawArc(CAFE.copy(alpha = 0.09f), 200f, 130f, false, Offset(cc.x - 22 * u, cc.y - 30 * u), Size(54 * u, 54 * u), style = Stroke(1.4f * u))
        // El matasellos con sus ondas.
        val ms = Offset(w - 52 * u, h - 56 * u)
        drawCircle(p.acento.copy(alpha = 0.17f), 17 * u, ms, style = Stroke(1.1f * u))
        drawCircle(p.acento.copy(alpha = 0.13f), 12.5f * u, ms, style = Stroke(0.6f * u))
        drawLine(p.acento.copy(alpha = 0.13f), Offset(ms.x - 9 * u, ms.y), Offset(ms.x + 9 * u, ms.y), 0.8f * u)
        for (k in 0 until 4) {
            val y0 = ms.y - 9 * u + k * 6 * u
            val onda = Path()
            var x = ms.x - 74 * u
            onda.moveTo(x, y0)
            while (x < ms.x - 19 * u) { onda.quadraticTo(x + 3.5f * u, y0 - 2.2f * u, x + 7 * u, y0); onda.quadraticTo(x + 10.5f * u, y0 + 2.2f * u, x + 14 * u, y0); x += 14 * u }
            drawPath(onda, p.acento.copy(alpha = 0.13f), style = Stroke(0.9f * u))
        }
        if (oscuro && !p.oscura) {
            penumbraCon(Offset(w * 0.94f, h * 0.04f), FAROL, 0.17f)
            // Las lamas de la persiana: rayas de la luz del farol que cruzan la hoja en diagonal.
            clipRect {
                rotate(30f, Offset(w * 0.94f, 0f)) {
                    for (k in 0 until 7) drawRect(FAROL.copy(alpha = 0.035f), Offset(w * 0.94f - 30 * u - k * 24 * u, -h * 0.05f), Size(9 * u, h * 0.75f))
                }
            }
        }
    }

    /** La raya fina de la crónica y, al pie de la barra, la liga elástica negra del quaderno con su tejido. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fondoBarraCon(p, oscuro)
        drawLine(p.tinta.copy(alpha = 0.3f), Offset(0f, h - 9.5f * u), Offset(w, h - 9.5f * u), 0.7f * u)
        val y0 = h - 6.5f * u
        drawRect(t(Color(0xFF17181A), oscuro && !p.oscura, noche), Offset(0f, y0), Size(w, 4.5f * u))
        var x = 0f
        while (x < w) { drawLine(Color.White.copy(alpha = 0.08f), Offset(x, y0), Offset(x + 3 * u, y0 + 4.5f * u), 0.5f * u); x += 2.2f * u }
        drawLine(Color.White.copy(alpha = 0.14f), Offset(0f, y0 + 0.6f * u), Offset(w, y0 + 0.6f * u), 0.5f * u)
    }

    /** El boleto del autobús: amarillo, con sus muescas a los lados, el picado y el timbre de la validadora en la esquina. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = h * 0.14f
        val forma = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h / 2 - r)
            arcTo(Rect(Offset(w, h / 2), r), 270f, -180f, false)
            lineTo(w, h); lineTo(0f, h); lineTo(0f, h / 2 + r)
            arcTo(Rect(Offset(0f, h / 2), r), 90f, -180f, false); close()
        }
        translate(1 * u, 1.6f * u) { drawPath(forma, Color.Black.copy(alpha = 0.22f)) }
        drawPath(forma, tonoBotonCon(BOLETOS[0], oscuro, noche))
        clipPath(forma) {
            drawRect(tonoBotonCon(TINTA, oscuro, noche).copy(alpha = 0.22f), Offset(0f, h - 5 * u), Size(w, 2.2f * u))
            var y = 3 * u
            while (y < h - 2 * u) { drawCircle(tonoBotonCon(TINTA, oscuro, noche).copy(alpha = 0.3f), 0.7f * u, Offset(8 * u, y)); y += 3 * u }
            rotate(-8f, Offset(w - 9 * u, 7 * u)) {
                drawRect(tonoBotonCon(ROJO, oscuro, noche).copy(alpha = 0.55f), Offset(w - 13 * u, 3 * u), Size(8 * u, 7 * u), style = Stroke(0.7f * u))
            }
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = TINTA

    // ---------------------------------------------------------------- la libreta de momentos

    /** La cartulina ocre del cuaderno de boletos, su elástico rojo, el mármol de la mesita del bar y el café. */
    private val OCRE = Color(0xFFC98A3A)
    private val ELASTICO = Color(0xFFA8432A)
    private val MARMOL = Color(0xFFECE7DD)
    private val MARMOL_VETA = Color(0xFF9C978C)
    private val AZULEJO = Color(0xFFD6E2D4)
    private val ESMALTE = Color(0xFF2F5A4A)
    private val CREMA_CAFE = Color(0xFFB98552)

    override val altoLibreta: Dp = 250.dp

    /**
     * El cuaderno de boletos sobre la mesita redonda del bar, de mármol veteado con su aro de aluminio, contra el muro de revoque
     * y el zócalo de azulejos blancos; arriba cuelga la lámpara de esmalte verde. A la derecha, la tazzina de espresso en su
     * plato con la cucharita y el sobre de azúcar; a la izquierda, el vaso de agua y un boleto suelto. El cuaderno, de cartulina
     * ocre con su elástico rojo y el tranvía estampado en seco, engorda con las hojas y deja asomar por arriba más boletos
     * guardados (uno más cada 10 hojas, hasta 10); la etiqueta blanca lleva el nombre y la cuenta. Al abrirse, el elástico se
     * suelta y la tapa gira sobre el lomo: dentro, la hoja de cuadritos con el sobre de los boletos pegado. En modo oscuro, el
     * bar de noche: la bombilla de la lámpara deja un círculo tibio sobre la mesa (uno de los dos degradados; el otro, el brillo
     * del mármol).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto)
        val lleno = llenoLibreta(e.hojas)
        // En modo oscuro, el bar de noche: todo bien hundido (la luz la pone la bombilla); el papel, menos, para leerse.
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0B0907), 0.42f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF0B0907), 0.3f) else x
        // El muro de revoque y el zócalo de azulejos con su listel de hierro.
        val zocalo = h * 0.2f
        drawRect(c(REVOQUE), Offset.Zero, Size(w, zocalo))
        for (k in 0 until 6) drawOval(c(REVOQUE_SOMBRA).copy(alpha = 0.22f), Offset(w * azar(71, k) - 22 * u, zocalo * 0.8f * azar(72, k) - 6 * u), Size(44 * u, 16 * u))
        drawRect(c(AZULEJO), Offset(0f, zocalo), Size(w, h - zocalo))
        var fila = 0; var y = zocalo + 3 * u
        while (y < h) {
            drawLine(c(TRAVERTINO_SOMBRA).copy(alpha = 0.8f), Offset(0f, y), Offset(w, y), 0.8f * u)
            var x = if (fila % 2 == 0) 0f else 11 * u
            while (x < w) { drawLine(c(TRAVERTINO_SOMBRA).copy(alpha = 0.8f), Offset(x, y), Offset(x, y + 10 * u), 0.8f * u); x += 22 * u }
            y += 10 * u; fila++
        }
        drawRect(c(HIERRO), Offset(0f, zocalo), Size(w, 3 * u))
        // La lámpara colgante de esmalte verde, con su cordón.
        val lampara = Offset(w * 0.5f, h * 0.075f)
        drawLine(c(HIERRO), Offset(lampara.x, 0f), Offset(lampara.x, lampara.y - 6 * u), 1.2f * u)
        val pantalla = Path().apply {
            moveTo(lampara.x - 5 * u, lampara.y - 7 * u); lineTo(lampara.x + 5 * u, lampara.y - 7 * u)
            quadraticTo(lampara.x + 8 * u, lampara.y, lampara.x + 22 * u, lampara.y + 6 * u); lineTo(lampara.x - 22 * u, lampara.y + 6 * u)
            quadraticTo(lampara.x - 8 * u, lampara.y, lampara.x - 5 * u, lampara.y - 7 * u); close()
        }
        drawPath(pantalla, c(ESMALTE))
        drawLine(c(PAPEL).copy(alpha = 0.5f), Offset(lampara.x - 21 * u, lampara.y + 6 * u), Offset(lampara.x + 21 * u, lampara.y + 6 * u), 1.4f * u)
        drawPath(Path().apply { moveTo(lampara.x - 3 * u, lampara.y - 5 * u); quadraticTo(lampara.x - 7 * u, lampara.y + 1 * u, lampara.x - 16 * u, lampara.y + 4.5f * u) }, Color.White.copy(alpha = 0.22f), style = Stroke(1.2f * u))
        if (oscuro) drawCircle(FAROL, 4.5f * u, Offset(lampara.x, lampara.y + 7 * u)) else drawCircle(c(PAPEL), 3.5f * u, Offset(lampara.x, lampara.y + 7 * u))
        // La mesita: el aro de aluminio y la tapa de mármol con sus vetas y su brillo.
        val mesa = Rect(Offset(w * 0.5f, h * 0.76f), 0f).let { Rect(it.center.x - w * 0.64f, it.center.y - h * 0.4f, it.center.x + w * 0.64f, it.center.y + h * 0.4f) }
        drawOval(Color.Black.copy(alpha = 0.12f), Offset(mesa.left + 6 * u, mesa.top + 10 * u), mesa.size)
        drawOval(c(mezcla(ALUMINIO, Color.Black, 0.4f)), Offset(mesa.left, mesa.top + 7 * u), mesa.size)
        drawOval(c(ALUMINIO), Offset(mesa.left, mesa.top + 2.5f * u), mesa.size)
        drawOval(c(MARMOL), mesa.topLeft, mesa.size)
        val forma = Path().apply { addOval(mesa) }
        clipPath(forma) {
            for (k in 0 until 7) {
                val x0 = w * (-0.1f + 0.2f * k) + 30 * u * azar(73, k)
                drawPath(Path().apply {
                    moveTo(x0, mesa.top); cubicTo(x0 + 40 * u, mesa.top + 40 * u, x0 - 30 * u, mesa.top + 90 * u, x0 + 60 * u, mesa.bottom)
                }, c(MARMOL_VETA).copy(alpha = 0.18f + 0.14f * azar(74, k)), style = Stroke((0.5f + 1.1f * azar(75, k)) * u))
            }
            drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Color.Transparent), Offset(w * 0.3f, mesa.top + h * 0.12f), w * 0.4f),
                mesa.topLeft, mesa.size)
        }
        // El círculo tibio de la bombilla sobre la mesa.
        if (oscuro) drawRect(Brush.radialGradient(listOf(FAROL.copy(alpha = 0.34f), FAROL.copy(alpha = 0.12f), Color.Transparent), Offset(w * 0.5f, h * 0.6f), w * 0.55f))
        // A la izquierda, el vaso de agua y un boleto suelto.
        val vaso = Offset(w * 0.1f, h * 0.6f)
        drawOval(Color.Black.copy(alpha = 0.1f), Offset(vaso.x - 13 * u, vaso.y + 18 * u), Size(30 * u, 10 * u))
        drawRect(c(VIDRIO).copy(alpha = 0.35f), Offset(vaso.x - 12 * u, vaso.y), Size(24 * u, 22 * u))
        drawRect(c(VIDRIO).copy(alpha = 0.25f), Offset(vaso.x - 12 * u, vaso.y + 7 * u), Size(24 * u, 15 * u))
        drawOval(c(VIDRIO).copy(alpha = 0.6f), Offset(vaso.x - 12 * u, vaso.y + 18 * u), Size(24 * u, 7 * u), style = Stroke(1 * u))
        drawOval(c(PAPEL).copy(alpha = 0.6f), Offset(vaso.x - 12 * u, vaso.y - 3.5f * u), Size(24 * u, 7 * u), style = Stroke(1.2f * u))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(vaso.x - 8 * u, vaso.y + 3 * u), Offset(vaso.x - 8 * u, vaso.y + 19 * u), 1.4f * u)
        rotate(14f, Offset(w * 0.1f, h * 0.88f)) {
            val o = Offset(w * 0.1f - 15 * u, h * 0.88f - 8 * u)
            drawRect(Color.Black.copy(alpha = 0.12f), o + Offset(1 * u, 1.5f * u), Size(30 * u, 16 * u))
            drawRect(c(BOLETOS[1]), o, Size(30 * u, 16 * u))
            drawRect(c(TINTA).copy(alpha = 0.3f), o + Offset(0f, 2.5f * u), Size(30 * u, 2.5f * u))
            drawRect(c(ROJO).copy(alpha = 0.6f), o + Offset(19 * u, 7 * u), Size(8 * u, 6 * u), style = Stroke(0.7f * u))
            drawLine(c(TINTA).copy(alpha = 0.4f), o + Offset(3 * u, 9 * u), o + Offset(15 * u, 9 * u), 0.7f * u)
        }
        // A la derecha, la tazzina en su plato con la cucharita, y el sobre de azúcar.
        val plato = Offset(w * 0.885f, h * 0.64f)
        drawOval(Color.Black.copy(alpha = 0.14f), Offset(plato.x - 24 * u, plato.y - 7 * u), Size(52 * u, 22 * u))
        drawOval(c(PAPEL), Offset(plato.x - 25 * u, plato.y - 10 * u), Size(50 * u, 20 * u))
        drawOval(c(TRAVERTINO_SOMBRA).copy(alpha = 0.6f), Offset(plato.x - 16 * u, plato.y - 6 * u), Size(32 * u, 12 * u), style = Stroke(0.8f * u))
        drawLine(c(ALUMINIO), Offset(plato.x + 6 * u, plato.y + 6 * u), Offset(plato.x + 22 * u, plato.y - 1 * u), 1.6f * u)
        drawOval(c(ALUMINIO), Offset(plato.x + 1 * u, plato.y + 4.5f * u), Size(8 * u, 5 * u))
        val taza = Path().apply {
            moveTo(plato.x - 12 * u, plato.y - 16 * u); lineTo(plato.x + 12 * u, plato.y - 16 * u)
            quadraticTo(plato.x + 11 * u, plato.y - 2 * u, plato.x, plato.y - 1 * u); quadraticTo(plato.x - 11 * u, plato.y - 2 * u, plato.x - 12 * u, plato.y - 16 * u); close()
        }
        drawPath(taza, c(PAPEL))
        drawPath(taza, c(TRAVERTINO_SOMBRA), style = Stroke(0.8f * u))
        drawArc(c(PAPEL), -80f, 170f, false, Offset(plato.x + 9 * u, plato.y - 15 * u), Size(9 * u, 9 * u), style = Stroke(2.2f * u))
        drawOval(c(PAPEL), Offset(plato.x - 12 * u, plato.y - 19 * u), Size(24 * u, 7 * u))
        drawOval(c(CAFE), Offset(plato.x - 10.5f * u, plato.y - 18.2f * u), Size(21 * u, 5.4f * u))
        drawOval(c(CREMA_CAFE), Offset(plato.x - 8 * u, plato.y - 17.6f * u), Size(14 * u, 3.6f * u))
        if (!oscuro) listOf(-3f, 3f).forEach { dx ->
            drawPath(Path().apply { moveTo(plato.x + dx * u, plato.y - 22 * u); quadraticTo(plato.x + (dx - 3) * u, plato.y - 27 * u, plato.x + dx * u, plato.y - 32 * u)
                quadraticTo(plato.x + (dx + 3) * u, plato.y - 37 * u, plato.x + dx * u, plato.y - 42 * u) }, Color.White.copy(alpha = 0.5f), style = Stroke(1 * u))
        }
        rotate(-18f, Offset(w * 0.89f, h * 0.88f)) {
            val o = Offset(w * 0.89f - 7 * u, h * 0.88f - 14 * u)
            drawRect(Color.Black.copy(alpha = 0.12f), o + Offset(1 * u, 1.5f * u), Size(14 * u, 28 * u))
            drawRect(c(PAPEL), o, Size(14 * u, 28 * u))
            drawRect(c(ROJO), o + Offset(0f, 9 * u), Size(14 * u, 10 * u))
            drawLine(c(TINTA).copy(alpha = 0.35f), o + Offset(0f, 2 * u), o + Offset(14 * u, 2 * u), 0.6f * u)
        }
        // El cuaderno, un poco girado sobre la mesa.
        val ancho = w * 0.58f; val alto = minOf(ancho * 0.6f, h * 0.48f)
        val r = Rect(w * 0.205f, h * 0.38f, w * 0.205f + ancho, h * 0.38f + alto)
        val g = (2.5f + 12f * lleno) * u
        val lomo = r.left + ancho * 0.07f
        rotate(-3f, r.center) {
            drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.18f), r.topLeft + Offset(4 * u, 5 * u + g), r.size, CornerRadius(4 * u))
            // Los boletos guardados que asoman por arriba (y alguno por la derecha): uno más cada 10 hojas.
            val boletos = (1 + e.hojas / 10).coerceAtMost(10)
            for (j in 0 until boletos) {
                val bw = ancho * 0.11f; val bh = alto * 0.36f
                val bx = r.left + ancho * (0.1f + 0.082f * j) + 4 * u * azar(e.semilla, 20 + j)
                val sale = (8f + 12f * azar(e.semilla, 30 + j)) * u * (1f - 0.4f * abre)
                val o = Offset(bx, r.top - sale)
                rotate(-10f + 20f * azar(e.semilla, 40 + j), Offset(o.x + bw / 2, r.top)) {
                    drawRect(c(BOLETOS[(j + (e.semilla and 3)) % BOLETOS.size]), o, Size(bw, bh))
                    drawRect(c(TINTA).copy(alpha = 0.25f), Offset(o.x, o.y + bh * 0.08f), Size(bw, bh * 0.06f))
                    drawLine(c(TINTA).copy(alpha = 0.4f), Offset(o.x + bw * 0.18f, o.y + bh * 0.24f), Offset(o.x + bw * 0.7f, o.y + bh * 0.24f), 0.7f * u)
                    drawRect(c(ROJO).copy(alpha = 0.5f), Offset(o.x + bw * 0.5f, o.y + bh * 0.3f), Size(bw * 0.38f, bh * 0.1f), style = Stroke(0.6f * u))
                }
            }
            if (e.hojas >= 40) for (j in 0 until (e.hojas / 40).coerceAtMost(3)) {
                val o = Offset(r.right - 6 * u, r.top + alto * (0.2f + 0.22f * j))
                rotate(-6f + 12f * azar(e.semilla, 50 + j), o) {
                    drawRect(c(BOLETOS[(j + 2) % BOLETOS.size]), o, Size(14 * u, alto * 0.14f))
                    drawRect(c(TINTA).copy(alpha = 0.25f), o + Offset(9 * u, 0f), Size(1.6f * u, alto * 0.14f))
                }
            }
            // El canto de las hojas y la tapa de atrás.
            drawRoundRect(c(mezcla(OCRE, Color.Black, 0.25f)), r.topLeft + Offset(0f, g), r.size, CornerRadius(4 * u))
            cantoLibreta(r, g * 0.85f, c(PAPEL), c(TRAVERTINO_SOMBRA))
            // Por dentro (al abrirse): la hoja de cuadritos con el margen rojo y el sobre de los boletos pegado.
            if (abre > 0.01f) {
                val hoja = Rect(lomo, r.top, r.right, r.bottom)
                drawRect(cp(PAPEL), hoja.topLeft, hoja.size)
                var gx = hoja.left + 6 * u
                while (gx < hoja.right) { drawLine(c(VIDRIO).copy(alpha = 0.45f), Offset(gx, hoja.top), Offset(gx, hoja.bottom), 0.5f * u); gx += 6 * u }
                var gy = hoja.top + 6 * u
                while (gy < hoja.bottom) { drawLine(c(VIDRIO).copy(alpha = 0.45f), Offset(hoja.left, gy), Offset(hoja.right, gy), 0.5f * u); gy += 6 * u }
                drawLine(c(ROJO).copy(alpha = 0.6f), Offset(hoja.left + 22 * u, hoja.top), Offset(hoja.left + 22 * u, hoja.bottom), 0.9f * u)
                val sobre = Rect(hoja.left + hoja.width * 0.42f, hoja.top + hoja.height * 0.42f, hoja.right - hoja.width * 0.08f, hoja.bottom - hoja.height * 0.08f)
                drawRect(c(BOLETOS[2]), Offset(sobre.left + 6 * u, sobre.top - 10 * u), Size(sobre.width * 0.3f, 16 * u))
                drawRect(c(BOLETOS[0]), Offset(sobre.left + sobre.width * 0.45f, sobre.top - 7 * u), Size(sobre.width * 0.3f, 14 * u))
                drawRect(c(mezcla(TRAVERTINO, PAPEL, 0.4f)), sobre.topLeft, sobre.size)
                drawPath(Path().apply { moveTo(sobre.left, sobre.top); lineTo(sobre.center.x, sobre.center.y); lineTo(sobre.right, sobre.top) }, c(TRAVERTINO_SOMBRA), style = Stroke(0.8f * u))
                for (k in 0 until 4) drawLine(c(TINTA).copy(alpha = 0.45f), Offset(hoja.left + 28 * u, hoja.top + 14 * u + k * 12 * u),
                    Offset(hoja.left + 28 * u + hoja.width * (0.5f - 0.08f * (k % 2)), hoja.top + 14 * u + k * 12 * u), 0.9f * u)
            }
            // La tapa de cartulina, que gira sobre el lomo.
            val sx = 1f - abre
            if (sx > 0.02f) scale(sx, 1f, Offset(lomo, r.center.y)) {
                drawRoundRect(c(OCRE), r.topLeft, r.size, CornerRadius(4 * u))
                var ty = r.top + 2 * u
                while (ty < r.bottom) { drawLine(Color.Black.copy(alpha = 0.045f), Offset(r.left, ty), Offset(r.right, ty), 0.6f * u); ty += 3 * u }
                // La etiqueta blanca con su marco rojo: el nombre y la cuenta.
                val et = Rect(r.left + ancho * 0.17f, r.top + alto * 0.14f, r.left + ancho * 0.83f, r.top + alto * 0.6f)
                drawRoundRect(Color.Black.copy(alpha = 0.18f), et.topLeft + Offset(1 * u, 1.5f * u), et.size, CornerRadius(5 * u))
                drawRoundRect(cp(PAPEL), et.topLeft, et.size, CornerRadius(5 * u))
                drawRoundRect(c(ELASTICO).copy(alpha = 0.8f), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, CornerRadius(3.5f * u), style = Stroke(1 * u))
                textoEn(medidor, e.nombre, Rect(et.left + 8 * u, et.top + 5 * u, et.right - 8 * u, et.top + et.height * 0.56f), letra, c(TINTA), 14f, peso = FontWeight.Bold, minimo = 8f)
                drawLine(c(ELASTICO).copy(alpha = 0.6f), Offset(et.left + 10 * u, et.top + et.height * 0.58f), Offset(et.right - 10 * u, et.top + et.height * 0.58f), 0.7f * u)
                textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, et.top + et.height * 0.62f, et.right - 8 * u, et.bottom - 5 * u), letra, c(ELASTICO), 10f, minimo = 6.5f)
                // El tranvía estampado en seco, abajo: la caja, las ventanillas, el trole y las ruedas.
                val tr = Rect(r.left + ancho * 0.36f, r.top + alto * 0.7f, r.left + ancho * 0.64f, r.top + alto * 0.86f)
                val luz = Color.White.copy(alpha = 0.2f); val som = Color.Black.copy(alpha = 0.2f)
                listOf(som to Offset(0.8f * u, 0.8f * u), luz to Offset.Zero).forEach { (col, o) ->
                    drawRoundRect(col, tr.topLeft + o, tr.size, CornerRadius(3 * u), style = Stroke(1 * u))
                    for (k in 0 until 5) drawRect(col, Offset(tr.left + tr.width * (0.08f + k * 0.18f), tr.top + tr.height * 0.18f) + o, Size(tr.width * 0.12f, tr.height * 0.32f))
                    drawLine(col, Offset(tr.center.x, tr.top) + o, Offset(tr.center.x + 10 * u, tr.top - 7 * u) + o, 0.9f * u)
                    listOf(0.22f, 0.78f).forEach { f -> drawCircle(col, 2 * u, Offset(tr.left + tr.width * f, tr.bottom + 1.5f * u) + o) }
                }
                if (abre > 0f) drawRoundRect(Color.Black.copy(alpha = 0.4f * abre), r.topLeft, r.size, CornerRadius(4 * u))
            }
            if (abre > 0.01f && sx > 0.02f) {
                val borde = lomo + (r.right - lomo) * sx
                drawRect(Color.Black.copy(alpha = 0.15f), Offset(borde, r.top), Size(16 * u * sin(PI.toFloat() * abre), r.height))
            }
            // El lomo de tela.
            drawRoundRect(c(mezcla(OCRE, Color.Black, 0.3f)), r.topLeft, Size(lomo - r.left + 1 * u, r.height), CornerRadius(4 * u))
            var lx = r.left + 1.5f * u
            while (lx < lomo) { drawLine(Color.White.copy(alpha = 0.06f), Offset(lx, r.top), Offset(lx, r.bottom), 0.5f * u); lx += 2 * u }
            // El elástico rojo, que al abrir se suelta hacia la derecha.
            val queda = 1f - suelta
            if (queda > 0f) {
                val xl = r.left + ancho * 0.88f + suelta * ancho * 0.16f
                drawRect(Color.Black.copy(alpha = 0.22f * queda), Offset(xl + 1.5f * u, r.top), Size(4.5f * u, r.height + g))
                drawRect(c(ELASTICO).copy(alpha = queda), Offset(xl, r.top - 2 * u), Size(4.5f * u, r.height + g + 3 * u))
                var ey = r.top
                while (ey < r.bottom + g) { drawLine(Color.Black.copy(alpha = 0.12f * queda), Offset(xl, ey), Offset(xl + 4.5f * u, ey + 1 * u), 0.5f * u); ey += 2.2f * u }
            }
        }
    }

    /**
     * La pestaña del día es un boleto de autobús de pie, con el borde de arriba picado: la letra de la semana en la franja de
     * color, el día grande al centro y, abajo, el timbre de la validadora en el color del ánimo (sin ánimo, el timbre vacío).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val giro = -3f + 6f * azar(e.semilla, 1)
        rotate(giro, Offset(w / 2, h / 2)) {
            val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
            val forma = Path().apply {
                moveTo(r.left, r.top); var x = r.left; var arriba = true
                while (x < r.right) { x = minOf(x + 3 * u, r.right); lineTo(x, if (arriba) r.top + 1.6f * u else r.top); arriba = !arriba }
                lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
            }
            translate(1 * u, 1.4f * u) { drawPath(forma, Color.Black.copy(alpha = 0.2f)) }
            drawPath(forma, c(BOLETOS[(e.semilla and 0x7fffffff) % BOLETOS.size]))
            drawRect(c(TINTA).copy(alpha = 0.78f), Offset(r.left, r.top + 5 * u), Size(r.width, 9 * u))
            textoEn(medidor, e.semana, Rect(r.left, r.top + 5 * u, r.right, r.top + 14 * u), letra, c(PAPEL), 8f, peso = FontWeight.Bold, minimo = 5f)
            textoEn(medidor, e.dia, Rect(r.left, r.top + 15 * u, r.right, r.top + 36 * u), letra, c(TINTA), 19f, peso = FontWeight.Bold, minimo = 9f)
            // Los agujeros del picado a la izquierda.
            var y = r.top + 17 * u
            while (y < r.top + 36 * u) { drawCircle(c(TINTA).copy(alpha = 0.28f), 0.6f * u, Offset(r.left + 2.5f * u, y)); y += 3 * u }
            val sello = Rect(r.left + 5 * u, r.bottom - 13 * u, r.right - 5 * u, r.bottom - 3.5f * u)
            if (e.tono != null) {
                drawRect(e.tono, sello.topLeft, sello.size)
                drawRect(c(TINTA).copy(alpha = 0.35f), sello.topLeft, sello.size, style = Stroke(0.7f * u))
            } else drawRect(c(ROJO).copy(alpha = 0.45f), sello.topLeft, sello.size, style = Stroke(0.8f * u))
        }
    }

    /** El mes, en la placa de travertino de una calle de Roma, atornillada al muro de revoque, con su marco y sus cuatro clavos. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(REVOQUE))
        for (k in 0 until 5) drawOval(c(REVOQUE_SOMBRA).copy(alpha = 0.2f), Offset(w * azar(81, k) - 20 * u, h * azar(82, k) - 5 * u), Size(40 * u, 10 * u))
        val medida = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)).size.width
        val ancho = (medida + 46 * u).coerceIn(w * 0.4f, w - 16 * u)
        val pl = Rect(w / 2 - ancho / 2, 3.5f * u, w / 2 + ancho / 2, h - 3.5f * u)
        drawRect(Color.Black.copy(alpha = 0.2f), pl.topLeft + Offset(1.2f * u, 1.6f * u), pl.size)
        drawRect(c(TRAVERTINO), pl.topLeft, pl.size)
        poros(pl, c(TRAVERTINO_PORO), 83)
        drawRect(c(TRAVERTINO_SOMBRA), pl.deflate(3 * u).topLeft, pl.deflate(3 * u).size, style = Stroke(1.2f * u))
        listOf(Offset(pl.left + 6.5f * u, pl.top + 6.5f * u), Offset(pl.right - 6.5f * u, pl.top + 6.5f * u),
            Offset(pl.left + 6.5f * u, pl.bottom - 6.5f * u), Offset(pl.right - 6.5f * u, pl.bottom - 6.5f * u)).forEach { p ->
            drawCircle(c(HIERRO), 1.6f * u, p); drawCircle(c(HIERRO_LUZ), 0.7f * u, p + Offset(-0.4f * u, -0.4f * u))
        }
        textoEn(medidor, texto, Rect(pl.left + 12 * u, pl.top + 4 * u, pl.right - 12 * u, pl.bottom - 4 * u), letra, c(TINTA), 13f, peso = FontWeight.Bold, espaciado = 1f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** La busta de correo aéreo, su forro, el spago con que se ata y la ceralacca. */
    private val BUSTA = Color(0xFFF1E4C8)
    private val BUSTA_SOMBRA = Color(0xFFC4AC7E)
    private val BUSTA_LUZ = Color(0xFFFBF3E0)
    private val SPAGO = Color(0xFFB59A6A)
    private val CERALACCA = Color(0xFFA8432A)
    private val AZUL_AEREO = Color(0xFF283F66)

    /** El sello de ceralacca: una gota de cera de borde irregular, el aro estampado, la R y su corona de puntos. */
    private fun DrawScope.ceralacca(c: Offset, r: Float, oscuro: Boolean, medidor: TextMeasurer, alfa: Float) {
        val u = density
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val gota = Path().apply {
            for (i in 0 until 16) {
                val a = (2 * PI * i / 16).toFloat()
                val rr = r * (0.92f + 0.14f * azar(i, 41))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        translate(0.8f * u, 1.2f * u) { drawPath(gota, Color.Black.copy(alpha = 0.25f * alfa)) }
        drawPath(gota, k(CERALACCA))
        drawCircle(k(mezcla(CERALACCA, Color.Black, 0.3f)), r * 0.7f, c, style = Stroke(r * 0.08f))
        for (i in 0 until 12) {
            val a = (2 * PI * i / 12).toFloat()
            drawCircle(k(mezcla(CERALACCA, BUSTA_LUZ, 0.35f)), r * 0.05f, Offset(c.x + cos(a) * r * 0.56f, c.y + sin(a) * r * 0.56f))
        }
        drawArc(k(Color.White.copy(alpha = 0.3f)), 200f, 70f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
        textoEn(medidor, "R", Rect(c.x - r * 0.5f, c.y - r * 0.5f, c.x + r * 0.5f, c.y + r * 0.5f), letra, k(mezcla(CERALACCA, Color.Black, 0.45f)), r * 0.7f / u, peso = FontWeight.Bold, minimo = 4f)
    }

    /** La orilla de correo aéreo: rombos en diagonal, rojo y verde de persiana con el crema en medio, a lo largo de [r]. */
    private fun DrawScope.orillaAerea(r: Rect, oscuro: Boolean, vertical: Boolean = false) {
        val paso = (if (vertical) r.width else r.height) * 2.6f
        var x = if (vertical) r.top else r.left; var i = 0
        val fin = if (vertical) r.bottom else r.right
        while (x < fin) {
            val col = t(if (i % 2 == 0) ROJO else PERSIANA, oscuro, false)
            val p = if (vertical) Path().apply { moveTo(r.left, x + paso * 0.5f); lineTo(r.right, x); lineTo(r.right, x + paso * 0.5f); lineTo(r.left, x + paso); close() }
            else Path().apply { moveTo(x, r.bottom); lineTo(x + paso * 0.5f, r.top); lineTo(x + paso, r.top); lineTo(x + paso * 0.5f, r.bottom); close() }
            drawPath(p, col)
            x += paso; i++
        }
    }

    /** El francobollo dentado: la estampilla con los tres pisos de arcos del anfiteatro y el valor; encima, el matasellos ondulado. */
    private fun DrawScope.francobollo(r: Rect, fondo: Color, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(Color.Black.copy(alpha = 0.15f), r.topLeft + Offset(0.6f * u, 0.8f * u), r.size)
        drawRect(c(BUSTA_LUZ), r.topLeft, r.size)
        val d = 0.75f * u
        var x = r.left + d * 1.5f
        while (x < r.right - d) { drawCircle(fondo, d, Offset(x, r.top)); drawCircle(fondo, d, Offset(x, r.bottom)); x += d * 3f }
        var y = r.top + d * 1.5f
        while (y < r.bottom - d) { drawCircle(fondo, d, Offset(r.left, y)); drawCircle(fondo, d, Offset(r.right, y)); y += d * 3f }
        val f = r.deflate(2.2f * u)
        drawRect(c(Color(0xFF3E6B8A)), f.topLeft, f.size)
        drawRect(c(TRAVERTINO), Offset(f.left, f.top + f.height * 0.3f), Size(f.width, f.height * 0.56f))
        for (piso in 0 until 3) {
            val y0 = f.top + f.height * (0.36f + piso * 0.17f)
            for (k in 0 until 4) {
                val ax = f.left + f.width * (0.1f + k * 0.21f)
                drawArc(c(TRAVERTINO_PORO), 180f, 180f, true, Offset(ax, y0), Size(f.width * 0.14f, f.height * 0.14f))
                drawRect(c(TRAVERTINO_PORO), Offset(ax, y0 + f.height * 0.07f), Size(f.width * 0.14f, f.height * 0.06f))
            }
        }
        drawRect(c(ROJO), Offset(f.left, f.bottom - f.height * 0.14f), Size(f.width, f.height * 0.14f))
        for (j in 0 until 4) {
            val yy = r.top + r.height * (0.2f + j * 0.2f)
            val onda = Path().apply {
                moveTo(r.left - r.width * 0.6f, yy)
                var xx = r.left - r.width * 0.6f
                while (xx < r.right + r.width * 0.2f) { quadraticTo(xx + 2 * u, yy - 1.4f * u, xx + 4 * u, yy); quadraticTo(xx + 6 * u, yy + 1.4f * u, xx + 8 * u, yy); xx += 8 * u }
            }
            drawPath(onda, c(TINTA).copy(alpha = 0.42f), style = Stroke(0.6f * u))
        }
        drawCircle(c(TINTA).copy(alpha = 0.4f), r.height * 0.42f, Offset(r.left - r.width * 0.15f, r.center.y), style = Stroke(0.6f * u))
    }

    /**
     * La busta de correo aéreo, vista del lado de la solapa: papel crema con la orilla de rombos rojos y verdes, la etiqueta
     * azul «PER VIA AEREA» y el francobollo del anfiteatro con su matasellos en la solapa recta; dentro, el forro ocre con su
     * trama de seguridad. La cierra la ceralacca roja con su R; las de al futuro, además, dan dos vueltas de spago con su
     * nudo de moño. Al abrirla, la ceralacca se parte (media se va con la solapa), la solapa se levanta detrás y sube la
     * carta. En modo oscuro, el papel se hunde en la penumbra del portal; la letra sigue oscura sobre crema.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val rad = CornerRadius(3 * u)
        val fondo = c(BUSTA)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(l + 1.5f * u, top + 2.5f * u), b.size, rad)
        // El forro de adentro, con su trama de seguridad: se ve al levantar la solapa.
        drawRoundRect(c(OCRE), b.topLeft, b.size, rad)
        clipRect(l, top, r, top + H * 0.45f) {
            var x = l - H; while (x < r) { drawLine(c(mezcla(OCRE, AZUL_AEREO, 0.45f)).copy(alpha = 0.35f), Offset(x, top), Offset(x + H * 0.5f, top + H * 0.5f), 0.7f * u); x += 3.2f * u }
            var x2 = l; while (x2 < r + H) { drawLine(c(mezcla(OCRE, AZUL_AEREO, 0.45f)).copy(alpha = 0.25f), Offset(x2, top), Offset(x2 - H * 0.5f, top + H * 0.5f), 0.7f * u); x2 += 3.2f * u }
        }
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val hondo = H * 0.4f
        val solapa = Path().apply {
            moveTo(l, top); lineTo(r, top); lineTo(r - W * 0.035f, top + hondo - 4 * u)
            quadraticTo(r - W * 0.04f, top + hondo, r - W * 0.06f, top + hondo); lineTo(l + W * 0.06f, top + hondo)
            quadraticTo(l + W * 0.04f, top + hondo, l + W * 0.035f, top + hondo - 4 * u); close()
        }
        val selloC = Offset(cx, top + hondo * k)
        val selloR = H * 0.13f
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            // La solapa levantada, por dentro: el forro ocre.
            drawPath(solapa, c(mezcla(OCRE, BUSTA_SOMBRA, 0.4f)))
            drawPath(solapa, c(BUSTA_SOMBRA), style = Stroke(1 * u))
        }
        // La carta.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.84f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 7 * u, 5.5f * u, 6 * u, oscuro)
            drawRect(c(ROJO).copy(alpha = 0.5f), Offset(h.left + 6 * u, h.top + 3 * u), Size(h.width * 0.22f, 1 * u))
        }
        // El frente: el bolsillo con su escote en V y las orillas aéreas.
        val v = top + H * 0.28f
        val frente = Path().apply {
            moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f); lineTo(r, bot - 3 * u)
            quadraticTo(r, bot, r - 3 * u, bot); lineTo(l + 3 * u, bot); quadraticTo(l, bot, l, bot - 3 * u); close()
        }
        drawPath(frente, fondo)
        clipPath(frente) {
            for (j in 0 until 14) { val y = top + H * azar(e.semilla, j); drawLine(c(BUSTA_SOMBRA).copy(alpha = 0.12f), Offset(l, y), Offset(r, y + (azar(e.semilla, j + 30) - 0.5f) * 4 * u), 0.6f * u) }
            orillaAerea(Rect(l, bot - 4 * u, r, bot), oscuro)
            orillaAerea(Rect(l, top, l + 4 * u, bot), oscuro, vertical = true)
            orillaAerea(Rect(r - 4 * u, top, r, bot), oscuro, vertical = true)
            drawLine(c(BUSTA_SOMBRA).copy(alpha = 0.6f), Offset(l + 4 * u, bot - 4 * u), Offset(cx - W * 0.1f, v + H * 0.05f), 0.8f * u)
            drawLine(c(BUSTA_SOMBRA).copy(alpha = 0.6f), Offset(r - 4 * u, bot - 4 * u), Offset(cx + W * 0.1f, v + H * 0.05f), 0.8f * u)
        }
        drawPath(Path().apply { moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f) }, c(BUSTA_SOMBRA), style = Stroke(1 * u))
        // La solapa cerrada (o cerrándose), con la etiqueta aérea y el francobollo.
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            drawPath(Path().apply { addPath(solapa); translate(Offset(0f, 1.2f * u)) }, Color.Black.copy(alpha = 0.12f))
            drawPath(solapa, c(BUSTA_LUZ))
            clipPath(solapa) {
                orillaAerea(Rect(l, top + hondo - 3.5f * u, r, top + hondo), oscuro)
                val et = Rect(l + W * 0.07f, top + H * 0.09f, l + W * 0.46f, top + H * 0.22f)
                drawRect(c(AZUL_AEREO), et.topLeft, et.size)
                drawRect(c(BUSTA_LUZ), et.deflate(1 * u).topLeft, et.deflate(1 * u).size, style = Stroke(0.5f * u))
                textoEn(medidor, "PER VIA AEREA", et.deflate(1.6f * u), letra, c(BUSTA_LUZ), 7f, peso = FontWeight.Bold, minimo = 4f)
                val fh = H * 0.26f
                francobollo(Rect(r - W * 0.06f - fh * 0.82f, top + H * 0.06f, r - W * 0.06f, top + H * 0.06f + fh), c(BUSTA_LUZ), oscuro)
            }
            drawPath(solapa, c(BUSTA_SOMBRA), style = Stroke(1 * u))
        }
        // El spago: dos vueltas de hilo de bramante a lo largo, con su moño al lado del sello.
        if (e.cordon > 0f) {
            val y0 = top + hondo
            listOf(-1.6f * u, 1.6f * u).forEachIndexed { i, dy ->
                val p = Path().apply { moveTo(l - 2 * u, y0 + dy); lineTo(r + 2 * u, y0 + dy + 0.6f * u) }
                trazoParcialCo(Path().apply { moveTo(l - 2 * u, y0 + dy + 1.2f * u); lineTo(r + 2 * u, y0 + dy + 1.8f * u) }, (e.cordon * 1.25f - i * 0.12f).coerceIn(0f, 1f), Color.Black.copy(alpha = 0.18f), 1.6f * u)
                trazoParcialCo(p, (e.cordon * 1.25f - i * 0.12f).coerceIn(0f, 1f), c(SPAGO), 1.4f * u)
            }
            val nudo = ((e.cordon - 0.75f) / 0.25f).coerceIn(0f, 1f)
            if (nudo > 0f) {
                val n0 = Offset(cx + selloR * 1.5f, y0)
                scale(nudo, nudo, n0) {
                    drawOval(c(SPAGO), Offset(n0.x - 1 * u, n0.y - 5.5f * u), Size(6 * u, 5 * u), style = Stroke(1.3f * u))
                    drawOval(c(SPAGO), Offset(n0.x - 1 * u, n0.y + 0.5f * u), Size(6 * u, 5 * u), style = Stroke(1.3f * u))
                    drawLine(c(SPAGO), n0, n0 + Offset(3 * u, 8 * u), 1.3f * u, StrokeCap.Round)
                    drawLine(c(SPAGO), n0, n0 + Offset(7 * u, 6 * u), 1.3f * u, StrokeCap.Round)
                    drawCircle(c(mezcla(SPAGO, NOGAL, 0.4f)), 1.6f * u, n0)
                }
            }
        }
        // La ceralacca, entera o partida: media se queda en el bolsillo, media se va con la solapa.
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(FAROL), e.brillo)
            selloPartidoCo(Offset(cx, top + hondo), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, selloC.y - (top + hondo) - selloR * 0.25f * e.romper),
                Offset(selloR * 0.2f * e.romper, selloR * 0.35f * e.romper), e.semilla) {
                ceralacca(Offset(cx, top + hondo), selloR, oscuro, medidor, e.alfaSello)
            }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            // Abierta: la mitad de abajo de la ceralacca sigue pegada al bolsillo.
            selloPartidoCo(Offset(cx, top + hondo), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.2f, selloR * 0.35f), e.semilla, conArriba = false) {
                ceralacca(Offset(cx, top + hondo), selloR, oscuro, medidor, e.alfaSello)
            }
        }
        // A quién va y las fechas, a pluma sobre el crema.
        textoEn(medidor, e.linea1, Rect(l + W * 0.08f, top + H * 0.6f, r - W * 0.08f, top + H * 0.76f), Letra.cronaca, c(TINTA), 11.5f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(l + W * 0.1f, top + H * 0.77f, r - W * 0.1f, top + H * 0.89f), letra, c(TINTA).copy(alpha = 0.78f), 9f, minimo = 6f)
    }

    override val cajaPrevia: Rect = Rect(0.15f, 0.235f, 0.85f, 0.39f)

    override val altoBuzon: Dp = 200.dp

    /** Una busta chica de pie en su casilla: lista (la ceralacca brilla), sellada (con su spago) o abierta (la carta asoma). */
    private fun DrawScope.bustaChica(r: Rect, tipo: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val inclina = (azar(semilla, 1) - 0.5f) * 6f
        rotate(inclina, r.center) {
            if (tipo == 2) {
                drawRect(c(PAPEL), Offset(r.left + r.width * 0.12f, r.top - r.height * 0.32f), Size(r.width * 0.76f, r.height * 0.6f))
                for (j in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, r.top - r.height * (0.22f - j * 0.12f)), Offset(r.right - r.width * 0.22f, r.top - r.height * (0.22f - j * 0.12f)), 0.6f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(0.8f * u, 1 * u), r.size)
            drawRect(c(BUSTA), r.topLeft, r.size)
            orillaAerea(Rect(r.left, r.bottom - 2 * u, r.right, r.bottom), oscuro)
            val punta = if (tipo == 2) r.top - r.height * 0.3f else r.top + r.height * 0.45f
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, punta); lineTo(r.right, r.top); close() }, c(if (tipo == 2) mezcla(OCRE, BUSTA, 0.3f) else BUSTA_LUZ))
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, punta); lineTo(r.right, r.top) }, c(BUSTA_SOMBRA), style = Stroke(0.7f * u))
            if (tipo == 1) {
                drawLine(c(SPAGO), Offset(r.left, r.top + r.height * 0.45f), Offset(r.right, r.top + r.height * 0.45f), 1.2f * u)
                drawLine(c(SPAGO), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 1.2f * u)
            }
            if (tipo != 2) drawCircle(c(CERALACCA), r.width * 0.13f, Offset(r.center.x, r.top + r.height * 0.45f))
            else drawArc(c(CERALACCA), 0f, 180f, true, Offset(r.center.x - r.width * 0.12f, r.top + r.height * 0.33f), Size(r.width * 0.24f, r.width * 0.24f))
        }
    }

    /**
     * El casellario de la portinería: el mueble de nogal de diez casillas, en dos filas, cada una con su placa de latón
     * numerada y su fondo hundido, contra el muro de revoque bajo el rótulo de travertino «PORTINERIA». En cada casilla, una
     * busta de pie: las listas con su ceralacca que brilla (el primer degradado), las selladas atadas con spago y las abiertas
     * con la carta asomando. Delante, el mostrador del portero con el registro abierto y el timbre de mesa, y en su frente tres
     * portaprecios de lata con la cuenta de cada clase. En modo oscuro, el portal de noche: solo la lámpara del mostrador
     * alumbra (el segundo degradado). Sin cartas, las casillas esperan vacías con sus números.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // El muro y la madera, de noche, más hundidos que las tarjetas (que se siguen leyendo).
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0A0807), 0.38f) else t(x, false, false)
        drawRect(m(REVOQUE))
        for (k in 0 until 9) drawOval(m(REVOQUE_SOMBRA).copy(alpha = 0.18f), Offset(w * azar(91, k) - 24 * u, h * 0.7f * azar(92, k)), Size(48 * u, 12 * u))
        // El rótulo de travertino.
        val rot = Rect(w * 0.3f, 6 * u, w * 0.7f, 24 * u)
        drawRect(Color.Black.copy(alpha = 0.2f), rot.topLeft + Offset(1 * u, 1.5f * u), rot.size)
        drawRect(c(TRAVERTINO), rot.topLeft, rot.size)
        poros(rot, c(TRAVERTINO_PORO), 93)
        drawRect(c(TRAVERTINO_SOMBRA), rot.deflate(2 * u).topLeft, rot.deflate(2 * u).size, style = Stroke(0.8f * u))
        textoEn(medidor, "PORTINERIA", rot.deflate(3 * u), letra, c(TINTA), 11f, peso = FontWeight.Bold, espaciado = 2f, minimo = 6f)
        // El casellario.
        val mueble = Rect(w * 0.06f, 30 * u, w * 0.94f, h * 0.7f)
        drawRect(Color.Black.copy(alpha = 0.25f), mueble.topLeft + Offset(2 * u, 3 * u), mueble.size)
        drawRect(m(NOGAL), mueble.topLeft, mueble.size)
        vetas(mueble, m(NOGAL_VETA).copy(alpha = 0.5f), 94, densidad = 0.7f)
        bisel(mueble, m(NOGAL_LUZ).copy(alpha = 0.7f), m(NOGAL_VETA), 2 * u)
        val cols = 5; val filas = 2
        val marco = 5 * u; val placa = 9 * u
        val cw = (mueble.width - marco * (cols + 1)) / cols
        val ch = (mueble.height - marco * (filas + 1) - placa * filas) / filas
        val cartas = cartasBuzonCo(e, cols * filas)
        for (f in 0 until filas) for (k in 0 until cols) {
            val i = f * cols + k
            val x0 = mueble.left + marco + k * (cw + marco)
            val y0 = mueble.top + marco + f * (ch + marco + placa)
            val hueco = Rect(x0, y0, x0 + cw, y0 + ch)
            drawRect(m(mezcla(NOGAL_VETA, Color.Black, 0.45f)), hueco.topLeft, hueco.size)
            drawRect(Color.Black.copy(alpha = 0.3f), hueco.topLeft, Size(cw, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), hueco.topLeft, Size(2.5f * u, ch))
            val tipo = cartas.getOrNull(i)
            if (tipo != null) {
                val bw = cw * 0.72f; val bh = minOf(ch * 0.62f, bw * 0.7f)
                val br = Rect(hueco.center.x - bw / 2, hueco.bottom - bh - 1 * u, hueco.center.x + bw / 2, hueco.bottom - 1 * u)
                if (tipo == 0) mancha(Offset(br.center.x, br.top + bh * 0.45f), cw * 0.55f, c(FAROL).copy(alpha = 0.55f))
                bustaChica(br, tipo, oscuro, i * 7 + 3)
            } else {
                // La casilla vacía: el gancho de latón con su llave.
                val g = Offset(hueco.center.x, hueco.top + ch * 0.25f)
                drawCircle(c(OCRE), 1.3f * u, g)
                drawCircle(c(OCRE).copy(alpha = 0.8f), 2.4f * u, g + Offset(0f, 4 * u), style = Stroke(0.9f * u))
                drawLine(c(OCRE).copy(alpha = 0.8f), g + Offset(0f, 6.4f * u), g + Offset(0f, 12 * u), 1 * u)
                drawLine(c(OCRE).copy(alpha = 0.8f), g + Offset(0f, 10.5f * u), g + Offset(2 * u, 10.5f * u), 1 * u)
            }
            val pl = Rect(hueco.center.x - 8 * u, hueco.bottom + 1.5f * u, hueco.center.x + 8 * u, hueco.bottom + placa - 0.5f * u)
            drawRect(c(OCRE), pl.topLeft, pl.size)
            drawRect(c(mezcla(OCRE, Color.White, 0.4f)).copy(alpha = 0.6f), pl.topLeft, Size(pl.width, 1 * u))
            textoEn(medidor, "${i + 1}", pl, letra, c(TINTA), 7f, peso = FontWeight.Bold, minimo = 5f)
        }
        // El mostrador del portero.
        val tablero = Rect(0f, h * 0.73f, w, h * 0.78f)
        drawRect(m(MARMOL), tablero.topLeft, tablero.size)
        vetas(tablero, m(MARMOL_VETA).copy(alpha = 0.4f), 95, densidad = 0.5f)
        val frente = Rect(0f, tablero.bottom, w, h)
        drawRect(m(NOGAL), frente.topLeft, frente.size)
        vetas(frente, m(NOGAL_VETA).copy(alpha = 0.45f), 96, densidad = 0.8f)
        drawRect(Color.Black.copy(alpha = 0.25f), frente.topLeft, Size(w, 2 * u))
        // El registro abierto y el timbre de mesa.
        val reg = Rect(w * 0.06f, tablero.top - 7 * u, w * 0.26f, tablero.top + 1 * u)
        drawRect(c(Color(0xFF283F66)), Offset(reg.left - 1.5f * u, reg.top + 1 * u), Size(reg.width + 3 * u, reg.height))
        drawRect(c(PAPEL), reg.topLeft, reg.size)
        drawLine(c(TINTA).copy(alpha = 0.4f), Offset(reg.center.x, reg.top), Offset(reg.center.x, reg.bottom), 0.6f * u)
        for (j in 0 until 3) { val yy = reg.top + 2 * u + j * 2 * u; drawLine(c(TINTA).copy(alpha = 0.25f), Offset(reg.left + 2 * u, yy), Offset(reg.right - 2 * u, yy), 0.5f * u) }
        val tim = Offset(w * 0.86f, tablero.top)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(tim.x - 9 * u, tim.y - 2 * u), Size(18 * u, 4 * u))
        drawArc(c(ALUMINIO), 180f, 180f, true, Offset(tim.x - 8 * u, tim.y - 9 * u), Size(16 * u, 16 * u))
        drawArc(Color.White.copy(alpha = 0.4f), 200f, 50f, false, Offset(tim.x - 6 * u, tim.y - 7 * u), Size(12 * u, 12 * u), style = Stroke(1 * u))
        drawCircle(c(HIERRO), 1.6f * u, Offset(tim.x, tim.y - 9.5f * u))
        // Los tres portaprecios con la cuenta de cada clase.
        val rotulos = listOf("DA APRIRE" to e.listas, "SIGILLATE" to e.selladas, "APERTE" to e.abiertas)
        val pw = minOf(w * 0.26f, 110 * u); val gap = (w - pw * 3) / 4
        rotulos.forEachIndexed { i, (txt, n) ->
            val pr = Rect(gap + i * (pw + gap), frente.top + 7 * u, gap + i * (pw + gap) + pw, h - 7 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), pr.topLeft + Offset(0.8f * u, 1.2f * u), pr.size)
            drawRect(c(ALUMINIO), pr.topLeft, pr.size)
            val tj = pr.deflate(2 * u)
            drawRect(c(PAPEL), tj.topLeft, tj.size)
            textoEn(medidor, txt, Rect(tj.left + 2 * u, tj.top, tj.left + tj.width * 0.68f, tj.bottom), letra, c(TINTA), 8.5f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 5f)
            textoEn(medidor, "$n", Rect(tj.left + tj.width * 0.68f, tj.top, tj.right - 2 * u, tj.bottom), Letra.cronaca, c(if (i == 0 && n > 0) ROJO else TINTA), 13f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        if (oscuro) {
            // La lámpara del mostrador, de noche: un círculo tibio sobre el casellario y el registro.
            mancha(Offset(w * 0.16f, tablero.top), w * 0.55f, c(FAROL).copy(alpha = 0.3f))
        }
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
 * La tapa es ese disquete de frente, con la orden «$ nano» impresa en la etiqueta y la fecha a bolígrafo; al abrirlo, el
 * obturador se corre y deja ver el disco girando.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 190.dp
    override val cajaTapa: Rect = Rect(0.13f, 0.55f, 0.87f, 0.845f)
    override val letraTapa: FontFamily = Letra.codigo
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = BOLIGRAFO
    override fun palabrasTapa(n: Int): String = if (n == 1) "[1 palabra]" else "[$n palabras]"

    /** El papel de la etiqueta de la tapa: hundido en oscuro y un poco verdoso de noche, pero claro para el bolígrafo. */
    private fun etiquetaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = ETIQUETA
        if (oscuro) r = mezcla(r, Color(0xFF050607), 0.24f)
        if (noche) r = mezcla(r, NOCHE, 0.12f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val i = (e.semilla and 0x7fffffff) % DISCOS.size
        val cuerpo = c(DISCOS[i])
        // El plástico beige pide relieves oscuros; los demás, claros.
        val relieve = if (i == 5) Color.Black else Color.White
        val d = Rect(w * 0.02f, h * 0.03f, w * 0.98f, h * 0.97f)
        // De noche, el resplandor del monitor cae sobre la mesa, detrás del disquete (el único degradado).
        if (e.noche) mancha(Offset(w * 0.28f, 0f), w * 0.6f, FOSFORO.copy(alpha = 0.16f))
        drawRoundRect(Color.Black.copy(alpha = 0.4f), d.topLeft + Offset(3 * u, 4 * u), d.size, CornerRadius(4 * u))
        // El cuerpo de plástico, con la esquina cortada arriba a la derecha.
        val ch = h * 0.075f; val rd = 3.5f * u
        val forma = Path().apply {
            moveTo(d.left + rd, d.top); lineTo(d.right - ch, d.top); lineTo(d.right, d.top + ch); lineTo(d.right, d.bottom - rd)
            quadraticTo(d.right, d.bottom, d.right - rd, d.bottom); lineTo(d.left + rd, d.bottom); quadraticTo(d.left, d.bottom, d.left, d.bottom - rd)
            lineTo(d.left, d.top + rd); quadraticTo(d.left, d.top, d.left + rd, d.top); close()
        }
        drawPath(forma, cuerpo)
        clipPath(forma) {
            var x = d.left + 2 * u
            while (x < d.right) { drawLine(relieve.copy(alpha = 0.03f), Offset(x, d.top), Offset(x, d.bottom), 0.6f * u); x += 2.4f * u }
            drawRect(relieve.copy(alpha = 0.07f), d.topLeft, Size(d.width, h * 0.03f))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(d.left, d.bottom - h * 0.025f), Size(d.width, h * 0.025f))
            // De noche, el reflejo del tubo cruza el plástico.
            if (e.noche) {
                drawLine(FOSFORO.copy(alpha = 0.08f), Offset(d.left + d.width * 0.02f, d.bottom), Offset(d.left + d.width * 0.2f, d.top), 16 * u)
                drawLine(FOSFORO.copy(alpha = 0.05f), Offset(d.left + d.width * 0.12f, d.bottom), Offset(d.left + d.width * 0.3f, d.top), 5 * u)
            }
        }
        drawPath(forma, relieve.copy(alpha = 0.16f), style = Stroke(1 * u))
        // El hueco del obturador y, en medio, la ranura por la que se ve el disco magnético.
        val hueco = Rect(w * 0.18f, d.top, w * 0.84f, h * 0.42f)
        drawRect(Color.Black.copy(alpha = 0.32f), hueco.topLeft, hueco.size)
        drawLine(relieve.copy(alpha = 0.14f), Offset(hueco.left, hueco.bottom), Offset(hueco.right, hueco.bottom), 0.8f * u)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(hueco.left, hueco.top), Offset(hueco.left, hueco.bottom), 0.8f * u)
        val ranura = Rect(w * 0.385f, h * 0.075f, w * 0.475f, h * 0.36f)
        drawRoundRect(c(Color(0xFF3A2E22)), ranura.topLeft, ranura.size, CornerRadius(2 * u))
        clipRect(ranura.left, ranura.top, ranura.right, ranura.bottom) {
            // Las pistas del disco, alrededor de su centro (escondido bajo la etiqueta), y el reflejo que gira al leerlo.
            val cd = Offset(w * 0.43f, h * 0.66f)
            for (k in 0 until 12) drawCircle(Color(0xFF7A6040).copy(alpha = if (k % 2 == 0) 0.35f else 0.18f), h * (0.32f + 0.024f * k), cd, style = Stroke(0.6f * u))
            rotate(-40f + 260f * a, cd) { drawLine(Color.White.copy(alpha = 0.16f), cd, Offset(cd.x, cd.y - h * 0.7f), 7 * u) }
            if (e.noche) drawRect(FOSFORO.copy(alpha = 0.06f), ranura.topLeft, ranura.size)
        }
        drawRoundRect(Color.Black.copy(alpha = 0.45f), ranura.topLeft, ranura.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        // El obturador de metal, que al abrirse se corre a la derecha y descubre el disco.
        val dx = (a * 1.6f).coerceAtMost(1f) * w * 0.26f
        val obt = Rect(w * 0.3f + dx, d.top, w * 0.56f + dx, h * 0.4f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(obt.left + 1.5f * u, obt.top), Size(obt.width, obt.height + 1.5f * u))
        drawRect(c(METAL), obt.topLeft, obt.size)
        var my = obt.top + 3 * u
        while (my < obt.bottom - 2 * u) { drawLine(Color.White.copy(alpha = 0.1f), Offset(obt.left, my), Offset(obt.right, my), 0.5f * u); my += 2.2f * u }
        drawRect(Color.White.copy(alpha = 0.4f), obt.topLeft, Size(obt.width, 1.4f * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(obt.left, obt.bottom - 1.6f * u), Size(obt.width, 1.6f * u))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(obt.right - 1.2f * u, obt.top), Size(1.2f * u, obt.height))
        // El rebaje del obturador y su pestaña estampada.
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(obt.left + obt.width * 0.08f, obt.top + obt.height * 0.22f), Size(obt.width * 0.14f, obt.height * 0.56f))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(obt.left + obt.width * 0.08f, obt.top + obt.height * 0.78f), Size(obt.width * 0.14f, 0.8f * u))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(obt.right - obt.width * 0.2f, obt.top + obt.height * 0.3f), Size(obt.width * 0.1f, obt.height * 0.4f), style = Stroke(0.7f * u))
        // Arriba a la izquierda, la flecha de inserción en relieve; de noche, una luna.
        val cf = Offset(w * 0.095f, h * 0.17f)
        val sf = h * 0.05f
        if (e.noche) {
            drawCircle(LUNA, sf, cf)
            drawCircle(cuerpo, sf * 0.84f, Offset(cf.x + sf * 0.45f, cf.y - sf * 0.25f))
            drawCircle(LUNA.copy(alpha = 0.7f), 0.9f * u, Offset(cf.x + sf * 1.6f, cf.y + sf * 0.9f))
        } else {
            val flecha = Path().apply { moveTo(cf.x - sf, cf.y - sf * 0.6f); lineTo(cf.x + sf, cf.y - sf * 0.6f); lineTo(cf.x, cf.y + sf * 0.9f); close() }
            drawPath(flecha, relieve.copy(alpha = 0.22f))
            drawPath(flecha, Color.Black.copy(alpha = 0.2f), style = Stroke(0.6f * u))
        }
        // La marca de alta densidad, en relieve, bajo la esquina cortada.
        textoEn(medidor, "HD", Rect(w * 0.865f, h * 0.17f, w * 0.965f, h * 0.29f), letra, relieve.copy(alpha = 0.3f), 11f, peso = FontWeight.Bold)
        drawRect(relieve.copy(alpha = 0.18f), Offset(w * 0.875f, h * 0.31f), Size(w * 0.08f, 0.8f * u))
        // Las ventanas de protección contra escritura, en las esquinas de abajo.
        val lv = h * 0.055f
        drawRect(Color.Black.copy(alpha = 0.75f), Offset(d.left + w * 0.022f, h * 0.87f), Size(lv, lv))
        drawRect(if (azar(e.semilla, 8) > 0.5f) Color.Black.copy(alpha = 0.75f) else relieve.copy(alpha = 0.2f), Offset(d.right - w * 0.022f - lv, h * 0.87f), Size(lv, lv))
        // La etiqueta: arriba, la franja impresa con la orden del editor; abajo, la fecha y las palabras a bolígrafo.
        val et = Rect(w * 0.1f, h * 0.445f, w * 0.9f, h * 0.955f)
        val papel = etiquetaTapa(oscuro, e.noche)
        drawRect(Color.Black.copy(alpha = 0.25f), et.topLeft + Offset(0.8f * u, 1.2f * u), et.size)
        drawRect(papel, et.topLeft, et.size)
        val banda = Rect(et.left, et.top, et.right, h * 0.525f)
        drawRect(c(FOSFORO_TENUE), banda.topLeft, banda.size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(banda.left, banda.bottom - 0.8f * u), Size(banda.width, 0.8f * u))
        val dia = e.fecha.substringBefore(" ").ifBlank { "hoy" }
        val orden = if (e.noche) "$ nano ~/suenos/$dia.dream" else "$ nano ~/diario/$dia.log"
        textoEn(medidor, orden, Rect(banda.left + 6 * u, banda.top, banda.right - 6 * u, banda.bottom), Letra.codigo, papel, 10f, peso = FontWeight.Bold, minimo = 6f)
        // El cursor de la orden, que parpadea en fósforo al abrirse.
        if (a > 0f) drawRect(FOSFORO.copy(alpha = a), Offset(banda.right - 10 * u, banda.top + banda.height * 0.25f), Size(5 * u, banda.height * 0.5f))
        drawLine(c(BOLIGRAFO).copy(alpha = 0.25f), Offset(et.left + 6 * u, h * 0.852f), Offset(et.right - 6 * u, h * 0.852f), 0.6f * u)
        textoEn(medidor, e.fecha, Rect(et.left + 6 * u, h * 0.86f, et.left + et.width * 0.64f, h * 0.94f), Letra.codigo, c(BOLIGRAFO).copy(alpha = 0.85f), 9.5f, minimo = 6f)
        if (e.palabras > 0 && !e.vacio) {
            textoEn(medidor, palabrasTapa(e.palabras), Rect(et.left + et.width * 0.66f, h * 0.86f, et.right - 6 * u, h * 0.94f), Letra.codigo, c(BOLIGRAFO).copy(alpha = 0.85f), 9.5f, minimo = 6f)
        }
        // De noche, la etiqueta recibe el fósforo del tubo en el borde de arriba.
        if (e.noche) drawRect(FOSFORO.copy(alpha = 0.12f), Offset(et.left, et.top), Size(et.width, 1.4f * u))
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 112.dp

    /** Un disquete chiquito (el emblema): el cuerpo, el obturador de metal arriba y la etiqueta abajo. */
    private fun DrawScope.disquete(o: Offset, s: Float, color: Color, fondo: Color) {
        drawRect(color, o, Size(s, s))
        drawRect(fondo, Offset(o.x + s * 0.28f, o.y), Size(s * 0.44f, s * 0.32f))
        drawRect(color, Offset(o.x + s * 0.52f, o.y + s * 0.06f), Size(s * 0.1f, s * 0.2f))
        drawRect(fondo.copy(alpha = 0.75f), Offset(o.x + s * 0.16f, o.y + s * 0.5f), Size(s * 0.68f, s * 0.42f))
    }

    /**
     * La ventana de la terminal: la barra de título con el disquete, la ruta del archivo y sus tres botones; dentro, la orden
     * («$ date +%A»), la fecha como salida en fósforo con su resplandor, el detalle como comentario («# viernes · hace 2 días»)
     * y el prompt con el cursor de bloque. De noche, «~/suenos/», la orden de dormir y una luna de fósforo con estrellas de
     * un píxel.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marco = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 2 * u)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(marco, CornerRadius(4 * u))) }
        drawPath(forma, mezcla(p.color, p.acento, 0.035f))
        clipPath(forma) {
            drawRect(p.acento.copy(alpha = 0.12f), marco.topLeft, Size(marco.width, 19 * u))
            var y = marco.top + 20 * u
            while (y < marco.bottom) { drawLine(p.acento.copy(alpha = 0.035f), Offset(marco.left, y), Offset(marco.right, y), 1f); y += 3 * u }
        }
        drawPath(forma, p.detalle.copy(alpha = 0.45f), style = Stroke(1 * u))
        drawLine(p.detalle.copy(alpha = 0.4f), Offset(marco.left, marco.top + 19 * u), Offset(marco.right, marco.top + 19 * u), 0.8f * u)
        disquete(Offset(marco.left + 6 * u, marco.top + 3.5f * u), 12 * u, p.detalle, p.color)
        textoEn(medidor, e.rotulo, Rect(marco.left + 22 * u, marco.top, marco.right - 40 * u, marco.top + 19 * u), Letra.codigo, p.detalle, 10f, minimo = 6.5f)
        // Los tres botones de la ventana: minimizar, maximizar y cerrar.
        val bx = marco.right - 35 * u
        for (k in 0..2) {
            val r = Rect(bx + k * 10.5f * u, marco.top + 6 * u, bx + k * 10.5f * u + 7 * u, marco.top + 13 * u)
            when (k) {
                0 -> drawLine(p.tenue, Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1 * u)
                1 -> drawRect(p.tenue, r.topLeft, r.size, style = Stroke(0.9f * u))
                else -> { drawLine(p.tenue, r.topLeft, r.bottomRight, 1 * u); drawLine(p.tenue, Offset(r.right, r.top), Offset(r.left, r.bottom), 1 * u) }
            }
        }
        val x0 = marco.left + 8 * u
        val ancho = marco.width - 16 * u
        // La orden.
        val xo = renglonCon(medidor, "$ ", x0, marco.top + 29 * u, ancho, Letra.codigo, p.acento, 10f)
        renglonCon(medidor, if (e.noche) "sleep --soñar 8h" else "date +%A", xo, marco.top + 29 * u, ancho - (xo - x0), Letra.codigo, p.tenue, 10f)
        // La luna de fósforo y sus estrellas de un píxel, a la derecha (de noche).
        val anchoFecha = if (e.noche) ancho - 34 * u else ancho
        if (e.noche) {
            val cl = Offset(marco.right - 20 * u, marco.top + 52 * u)
            drawCircle(p.acento.copy(alpha = 0.08f), 15 * u, cl)
            crecienteCon(cl, 9 * u, p.acento.copy(alpha = 0.9f))
            listOf(Offset(-16f, -18f), Offset(8f, -22f), Offset(12f, 14f), Offset(-12f, 18f), Offset(-22f, 2f)).forEach { k ->
                drawRect(p.tinta.copy(alpha = 0.8f), Offset(cl.x + k.x * u, cl.y + k.y * u), Size(1.6f * u, 1.6f * u))
            }
        }
        // La fecha, salida de la orden: con el resplandor del fósforo detrás.
        val yf = marco.top + 51 * u
        val tamF = 19f
        listOf(Offset(-0.9f, 0f), Offset(0.9f, 0f), Offset(0f, 0.9f)).forEach { d ->
            renglonCon(medidor, e.fecha, x0 + d.x * u, yf + d.y * u, anchoFecha, Letra.codigo, p.acento.copy(alpha = 0.2f), tamF, minimo = 9f)
        }
        renglonCon(medidor, e.fecha, x0, yf, anchoFecha, Letra.codigo, p.tinta, tamF, minimo = 9f)
        // El detalle, como comentario.
        renglonCon(medidor, "# " + e.detalle, x0, marco.top + 75 * u, ancho, Letra.codigo, p.tenue, 10.5f, minimo = 6.5f)
        // El prompt y el cursor de bloque.
        val xp = renglonCon(medidor, if (e.noche) "vm@vestigium:~/suenos$" else "vm@vestigium:~/diario$", x0, marco.top + 94 * u, ancho - 10 * u, Letra.codigo, p.acento, 10f)
        drawRect(p.acento.copy(alpha = 0.85f), Offset(xp + 4 * u, marco.top + 88.5f * u), Size(6.5f * u, 11 * u))
    }

    /**
     * La pantalla de fósforo: el resplandor del tubo arriba, las esquinas que se hunden en la curva del vidrio, las líneas
     * de barrido, la raya del canal de los números de línea, unos píxeles vivos, las escuadras de las esquinas y la línea
     * de estado al pie. El papel ya es oscuro: solo van los adornos.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.radialGradient(listOf(p.acento.copy(alpha = if (noche) 0.05f else 0.075f), Color.Transparent), center = Offset(w / 2, h * 0.2f), radius = maxOf(w, h) * 0.6f))
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.6f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.6f), center = Offset(w / 2, h / 2), radius = hypot(w, h) / 2))
        var y = 0f
        while (y < h) { drawLine(p.acento.copy(alpha = 0.035f), Offset(0f, y), Offset(w, y), 1f); y += 3 * u }
        // El canal de los números de línea.
        drawLine(p.tenue.copy(alpha = 0.4f), Offset(46 * u, 0f), Offset(46 * u, h), 0.8f * u)
        // Píxeles vivos, siempre los mismos.
        for (k in 0 until 40) {
            drawRect(p.detalle.copy(alpha = 0.08f + 0.18f * azar(37, k)), Offset(w * azar(37, k + 100), h * azar(37, k + 200)), Size(1.5f * u, 1.5f * u))
        }
        // Las escuadras de las esquinas.
        val m = 8 * u; val l = 16 * u
        listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, d) ->
            drawLine(p.tenue.copy(alpha = 0.55f), o, Offset(o.x + d.x * l, o.y), 1.2f * u)
            drawLine(p.tenue.copy(alpha = 0.55f), o, Offset(o.x, o.y + d.y * l), 1.2f * u)
        }
        // La línea de estado: el bloque del modo a la izquierda y la posición a la derecha.
        val sb = Rect(0f, h - 22 * u, w, h - 8 * u)
        drawRect(p.acento.copy(alpha = 0.07f), sb.topLeft, sb.size)
        drawRect(p.acento.copy(alpha = 0.45f), Offset(sb.left + 12 * u, sb.top + 3 * u), Size(30 * u, sb.height - 6 * u))
        for (k in 0 until 4) drawRect(p.tenue.copy(alpha = 0.5f), Offset(sb.right - 16 * u - k * 7 * u, sb.top + 4 * u), Size(4.5f * u, sb.height - 8 * u))
    }

    /** La barra de pestañas de la terminal: oscura, con sus líneas de barrido y la raya de fósforo al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(mezcla(p.color, p.acento, 0.03f), mezcla(p.color, p.acento, 0.1f))))
        var y = 0f
        while (y < h) { drawLine(p.acento.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 1f); y += 3 * u }
        drawLine(p.acento.copy(alpha = 0.14f), Offset(0f, h - 2.5f * u), Offset(w, h - 2.5f * u), 2.5f * u)
        drawLine(p.acento.copy(alpha = 0.6f), Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 1 * u)
        for (k in 0 until 3) drawRect(p.tenue.copy(alpha = 0.6f), Offset(w - 10 * u - k * 6 * u, 3 * u), Size(3.5f * u, 3.5f * u))
    }

    /** Una opción resaltada de la consola: el bloque de fósforo encendido, con sus corchetes oscuros y su resplandor. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(p.acento.copy(alpha = 0.16f), Offset(-1.5f * u, -1.5f * u), Size(w + 3 * u, h + 3 * u), style = Stroke(2.5f * u))
        drawRect(p.acento.copy(alpha = 0.9f), Offset(1 * u, 1 * u), Size(w - 2 * u, h - 2 * u))
        var y = 2 * u
        while (y < h - 1 * u) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(1 * u, y), Offset(w - 1 * u, y), 1f); y += 3 * u }
        val osc = Color(0xFF021417)
        listOf(5 * u to 1f, w - 5 * u to -1f).forEach { (x, d) ->
            drawLine(osc, Offset(x, 7 * u), Offset(x, h - 7 * u), 1.4f * u)
            drawLine(osc, Offset(x, 7 * u), Offset(x + d * 3 * u, 7 * u), 1.4f * u)
            drawLine(osc, Offset(x, h - 7 * u), Offset(x + d * 3 * u, h - 7 * u), 1.4f * u)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFF021417)

    // ---------------------------------------------------------------- la libreta de momentos

    /** El papel continuo de la impresora, la taza y las teclas de acento del teclado. */
    private val PAPEL_CONTINUO = Color(0xFFD9DEE3)
    private val TAZA = Color(0xFF7A2A30)
    private val TECLA = Color(0xFF2A2D31)
    private val TECLA_CLARA = Color(0xFFB7B09A)
    private val AMBAR = Color(0xFFE8A33A)

    override val altoLibreta: Dp = 252.dp

    /**
     * La consola de registro sobre el escritorio, de noche (el tema solo tiene modo oscuro, así que [oscuro] no cambia nada):
     * el monitor de tubo de carcasa beige con su pantalla de fósforo y, a la derecha, la impresora de matriz con el papel
     * continuo plegado en acordeón detrás (un pliegue más cada 6 hojas, hasta 26) y la hoja impresa que sale por arriba.
     * Delante, el teclado mecánico de teclas grises con el Esc y el Enter de color, su cable enroscado, y la taza. Cerrada, la
     * consola está en reposo: la pantalla apenas brilla con el nombre y la cuenta y el piloto es ámbar. Al abrirse se
     * enciende: el piloto se pone verde, la pantalla resplandece y baña el escritorio (los dos degradados: el halo del tubo y
     * su luz sobre la mesa), se escriben los renglones del registro con su cursor y la impresora saca más papel.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val lleno = llenoLibreta(e.hojas)
        val luz = 0.25f + 0.75f * a
        // La pared del cuarto y el escritorio de madera oscura con sus vetas.
        drawRect(FONDO)
        var x = 0f
        while (x < w) { drawLine(Color.White.copy(alpha = 0.018f), Offset(x, 0f), Offset(x, h * 0.6f), 1 * u); x += 26 * u }
        val tablero = h * 0.6f
        drawRect(MESA, Offset(0f, tablero), Size(w, h - tablero))
        vetas(Rect(0f, tablero + 4 * u, w, h), MESA_VETA, 19, densidad = 0.7f)
        drawRect(mezcla(MESA, Color.White, 0.12f), Offset(0f, tablero), Size(w, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, tablero + 2 * u), Size(w, 3 * u))
        // El resplandor del tubo sobre el escritorio y la pared.
        val centro = Offset(w * 0.4f, h * 0.32f)
        drawRect(Brush.radialGradient(listOf(FOSFORO.copy(alpha = 0.28f * luz), FOSFORO.copy(alpha = 0.08f * luz), Color.Transparent), centro, w * 0.62f))
        // La impresora de matriz, a la derecha, con el papel continuo en acordeón detrás y la hoja impresa que sale.
        val imp = Rect(w * 0.66f, h * 0.44f, w * 0.97f, h * 0.66f)
        val pliegues = (2 + e.hojas / 6).coerceAtMost(26)
        val grueso = 1.7f * u
        val pila = Rect(imp.left + 6 * u, imp.top - pliegues * grueso + 4 * u, imp.right - 6 * u, imp.top + 4 * u)
        for (k in 0 until pliegues) {
            val y = pila.bottom - (k + 1) * grueso
            val corre = if (k % 2 == 0) 0f else 1.4f * u
            drawRect(mezcla(PAPEL_CONTINUO, Color.Black, if (k % 2 == 0) 0.45f else 0.6f), Offset(pila.left + corre, y), Size(pila.width, grueso))
        }
        val sale = (14f + 30f * lleno + 34f * a) * u
        val hoja = Rect(imp.left + imp.width * 0.16f, imp.top - sale, imp.right - imp.width * 0.16f, imp.top + 4 * u)
        drawRect(mezcla(PAPEL_CONTINUO, Color.Black, 0.25f - 0.12f * a), hoja.topLeft, hoja.size)
        var yy = hoja.top + 3 * u
        while (yy < hoja.bottom - 2 * u) {
            listOf(hoja.left + 3 * u, hoja.right - 3 * u).forEach { drawCircle(FONDO, 1 * u, Offset(it, yy)) }
            yy += 5 * u
        }
        yy = hoja.top + 5 * u; var k = 0
        while (yy < hoja.bottom - 6 * u) {
            drawLine(Color(0xFF2A2F36).copy(alpha = 0.55f), Offset(hoja.left + 8 * u, yy), Offset(hoja.right - (if (k % 3 == 1) 22 else 9) * u, yy), 0.8f * u)
            yy += 4 * u; k++
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(imp.left + 4 * u, imp.bottom), Size(imp.width, 4 * u))
        drawRoundRect(CARCASA_SOMBRA, imp.topLeft, imp.size, CornerRadius(4 * u))
        drawRoundRect(CARCASA, imp.topLeft, Size(imp.width, imp.height * 0.55f), CornerRadius(4 * u))
        drawRect(PLASTICO_OSC, Offset(imp.left + imp.width * 0.12f, imp.top + 3 * u), Size(imp.width * 0.76f, 4 * u))
        drawRoundRect(AHUMADO.copy(alpha = 0.8f), Offset(imp.left + imp.width * 0.1f, imp.top + imp.height * 0.22f), Size(imp.width * 0.8f, imp.height * 0.24f), CornerRadius(2 * u))
        listOf(0f, 1f).forEach { s -> drawCircle(PLASTICO, 4 * u, Offset(if (s == 0f) imp.left + 1 * u else imp.right - 1 * u, imp.top + imp.height * 0.32f)) }
        for (j in 0 until 3) drawRoundRect(if (j == 0) mezcla(PILOTO, Color.Black, 0.7f - 0.6f * a) else CARCASA_SOMBRA,
            Offset(imp.left + imp.width * (0.56f + j * 0.11f), imp.top + imp.height * 0.7f), Size(imp.width * 0.08f, 4 * u), CornerRadius(1 * u))
        for (j in 0 until 6) drawLine(Color.Black.copy(alpha = 0.25f), Offset(imp.left + imp.width * (0.1f + j * 0.06f), imp.top + imp.height * 0.66f),
            Offset(imp.left + imp.width * (0.1f + j * 0.06f), imp.bottom - 5 * u), 1 * u)
        // El monitor de tubo: carcasa beige, el marco, la pantalla y su pie.
        val mon = Rect(w * 0.06f, h * 0.035f, w * 0.63f, h * 0.6f)
        drawPath(Path().apply { moveTo(mon.center.x - mon.width * 0.2f, mon.bottom - 4 * u); lineTo(mon.center.x + mon.width * 0.2f, mon.bottom - 4 * u)
            lineTo(mon.center.x + mon.width * 0.28f, tablero + 6 * u); lineTo(mon.center.x - mon.width * 0.28f, tablero + 6 * u); close() }, CARCASA_SOMBRA)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), mon.topLeft + Offset(4 * u, 5 * u), mon.size, CornerRadius(9 * u))
        drawRoundRect(CARCASA_SOMBRA, mon.topLeft, mon.size, CornerRadius(9 * u))
        drawRoundRect(CARCASA, mon.topLeft, Size(mon.width - 3 * u, mon.height - 4 * u), CornerRadius(8 * u))
        drawRoundRect(mezcla(CARCASA, FOSFORO, 0.12f * luz), mon.topLeft, Size(mon.width - 3 * u, mon.height - 4 * u), CornerRadius(8 * u), style = Stroke(1 * u))
        val marco = Rect(mon.left + mon.width * 0.07f, mon.top + mon.height * 0.08f, mon.right - mon.width * 0.07f, mon.bottom - mon.height * 0.2f)
        drawRoundRect(CARCASA_SOMBRA, marco.inflate(3 * u).topLeft, marco.inflate(3 * u).size, CornerRadius(8 * u))
        drawRoundRect(PLASTICO_OSC, marco.topLeft, marco.size, CornerRadius(7 * u))
        val pan = marco.deflate(5 * u)
        drawRoundRect(mezcla(PANTALLA, FOSFORO, 0.05f * luz), pan.topLeft, pan.size, CornerRadius(10 * u))
        // Lo escrito en el fósforo: el nombre, la cuenta y los renglones del registro que aparecen al encenderse.
        val tinta = mezcla(FOSFORO_TENUE, FOSFORO, a)
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pan, CornerRadius(10 * u))) }) {
            val m = 9 * u
            renglonCon(medidor, "> " + e.nombre.lowercase(), pan.left + m, pan.top + 13 * u, pan.width - 2 * m, letra, tinta, 13f, peso = FontWeight.Bold, minimo = 7f)
            renglonCon(medidor, "  " + e.cuenta, pan.left + m, pan.top + 28 * u, pan.width - 2 * m, letra, tinta.copy(alpha = 0.85f), 10f, minimo = 6.5f)
            drawLine(tinta.copy(alpha = 0.5f), Offset(pan.left + m, pan.top + 37 * u), Offset(pan.right - m, pan.top + 37 * u), 0.8f * u)
            val renglones = ((pan.height - 48 * u) / (9 * u)).toInt().coerceAtLeast(1)
            val escritos = (renglones * a).toInt()
            for (j in 0 until escritos) {
                val y = pan.top + 46 * u + j * 9 * u
                renglonCon(medidor, "[ok]", pan.left + m, y, 30 * u, letra, PILOTO.copy(alpha = 0.85f), 8f, minimo = 5f)
                val largo = (pan.width - 2 * m - 34 * u) * (0.35f + 0.6f * azar(e.semilla, 60 + j))
                drawLine(FOSFORO.copy(alpha = 0.55f), Offset(pan.left + m + 34 * u, y), Offset(pan.left + m + 34 * u + largo, y), 2.4f * u)
            }
            val yc = pan.top + 46 * u + escritos * 9 * u
            if (yc < pan.bottom - 4 * u) drawRect(tinta, Offset(pan.left + m, yc - 4 * u), Size(6 * u, 8 * u))
            var ys = pan.top
            while (ys < pan.bottom) { drawLine(Color.Black.copy(alpha = 0.28f), Offset(pan.left, ys), Offset(pan.right, ys), 0.6f * u); ys += 2.2f * u }
            // El reflejo curvo del vidrio del tubo.
            drawPath(Path().apply { moveTo(pan.left + 6 * u, pan.top + pan.height * 0.6f); quadraticTo(pan.left + 8 * u, pan.top + 8 * u, pan.left + pan.width * 0.45f, pan.top + 5 * u) },
                Color.White.copy(alpha = 0.07f), style = Stroke(5 * u))
        }
        // La franja de abajo del monitor: las ranuras, la perilla y el piloto (ámbar en reposo, verde encendido).
        val pie = mon.bottom - mon.height * 0.12f
        for (j in 0 until 5) drawLine(CARCASA_SOMBRA, Offset(mon.left + mon.width * (0.1f + j * 0.035f), pie - 3 * u), Offset(mon.left + mon.width * (0.1f + j * 0.035f), pie + 4 * u), 1.2f * u)
        drawCircle(CARCASA_SOMBRA, 4 * u, Offset(mon.right - mon.width * 0.2f, pie))
        val piloto = Offset(mon.right - mon.width * 0.1f, pie)
        drawCircle(if (a > 0.05f) PILOTO else AMBAR, 2.4f * u, piloto)
        drawCircle((if (a > 0.05f) PILOTO else AMBAR).copy(alpha = 0.25f), 5 * u, piloto)
        // El teclado mecánico, en perspectiva, con su cable enroscado que sube al monitor.
        val tec = Rect(w * 0.1f, h * 0.73f, w * 0.64f, h * 0.95f)
        drawPath(Path().apply { moveTo(tec.center.x, tec.top); cubicTo(tec.center.x + 6 * u, tec.top - 8 * u, mon.center.x - 20 * u, tablero + 10 * u, mon.center.x - 34 * u, tablero + 3 * u) },
            Color(0xFF15171A), style = Stroke(2 * u))
        val sesgo = 8 * u
        val caja = Path().apply { moveTo(tec.left + sesgo, tec.top); lineTo(tec.right - sesgo, tec.top); lineTo(tec.right, tec.bottom); lineTo(tec.left, tec.bottom); close() }
        translate(2 * u, 4 * u) { drawPath(caja, Color.Black.copy(alpha = 0.4f)) }
        drawPath(caja, PLASTICO_OSC)
        drawPath(caja, FOSFORO.copy(alpha = 0.08f * luz))
        val filas = 5; val cols = 15
        for (f in 0 until filas) {
            val y0 = tec.top + 3 * u + f * (tec.height - 6 * u) / filas
            val y1 = y0 + (tec.height - 6 * u) / filas - 1.4f * u
            val izq = tec.left + sesgo * (1f - (f + 0.5f) / filas) + 3 * u
            val der = tec.right - sesgo * (1f - (f + 0.5f) / filas) - 3 * u
            val paso = (der - izq) / cols
            var cx = izq; var j = 0
            while (cx < der - 1 * u) {
                val ancho = when {
                    f == 4 && j == 3 -> paso * 6f
                    f == 2 && j == 0 -> paso * 1.6f
                    f == 2 && cx + paso * 2.2f >= der -> der - cx
                    else -> paso
                }
                val esc = f == 0 && j == 0; val intro = f == 2 && cx + ancho >= der - 1 * u
                val col = when { esc -> ROJO_TECLA; intro -> FOSFORO_TENUE; f == 4 || j == 0 || cx + ancho >= der - 1 * u -> TECLA_CLARA; else -> TECLA }
                drawRoundRect(mezcla(col, Color.Black, 0.35f), Offset(cx + 0.5f * u, y0 + 0.8f * u), Size(ancho - 1.3f * u, y1 - y0), CornerRadius(1.2f * u))
                drawRoundRect(col, Offset(cx + 0.5f * u, y0), Size(ancho - 1.3f * u, (y1 - y0) * 0.8f), CornerRadius(1.2f * u))
                drawRoundRect(Color.White.copy(alpha = 0.08f), Offset(cx + 1.2f * u, y0 + 0.6f * u), Size(ancho - 2.7f * u, (y1 - y0) * 0.35f), CornerRadius(1 * u))
                cx += ancho; j++
            }
        }
        // La taza, con su asa y un hilo de vapor.
        val tz = Offset(w * 0.8f, h * 0.83f)
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(tz.x - 15 * u, tz.y + 16 * u), Size(34 * u, 8 * u))
        drawArc(TAZA, -70f, 140f, false, Offset(tz.x + 8 * u, tz.y - 2 * u), Size(16 * u, 16 * u), style = Stroke(3.5f * u))
        drawRect(TAZA, Offset(tz.x - 14 * u, tz.y - 4 * u), Size(28 * u, 24 * u))
        drawOval(TAZA, Offset(tz.x - 14 * u, tz.y + 16 * u), Size(28 * u, 8 * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(tz.x - 10 * u, tz.y), Size(4 * u, 20 * u))
        drawRect(FOSFORO.copy(alpha = 0.12f * luz), Offset(tz.x - 14 * u, tz.y - 4 * u), Size(6 * u, 26 * u))
        drawOval(mezcla(TAZA, Color.White, 0.15f), Offset(tz.x - 14 * u, tz.y - 8 * u), Size(28 * u, 8 * u))
        drawOval(Color(0xFF1E120C), Offset(tz.x - 11.5f * u, tz.y - 6.8f * u), Size(23 * u, 5.6f * u))
        textoEn(medidor, "λ", Rect(tz.x - 8 * u, tz.y + 1 * u, tz.x + 8 * u, tz.y + 17 * u), letra, mezcla(TAZA, Color.White, 0.55f), 12f)
        drawPath(Path().apply { moveTo(tz.x, tz.y - 10 * u); quadraticTo(tz.x - 5 * u, tz.y - 17 * u, tz.x, tz.y - 24 * u); quadraticTo(tz.x + 5 * u, tz.y - 31 * u, tz.x, tz.y - 38 * u) },
            Color.White.copy(alpha = 0.12f), style = Stroke(1.2f * u))
    }

    private val ROJO_TECLA = Color(0xFFB8343A)

    /**
     * La pestaña del día es una ficha de la terminal: un recuadro negro con el borde de fósforo y una esquina cortada (a un
     * lado u otro según la semilla), la letra de la semana entre corchetes, el día grande y abajo el led del ánimo en su color
     * (sin ánimo, un guion apagado).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 3 * u, w - 3 * u, h - 3 * u)
        val corte = 7 * u
        val izq = (e.semilla and 1) == 0
        val forma = Path().apply {
            if (izq) { moveTo(r.left + corte, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + corte) }
            else { moveTo(r.left, r.top); lineTo(r.right - corte, r.top); lineTo(r.right, r.top + corte); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom) }
            close()
        }
        drawPath(forma, PANTALLA)
        drawPath(forma, FOSFORO_TENUE, style = Stroke(1 * u))
        textoEn(medidor, "[" + e.semana + "]", Rect(r.left, r.top + 3 * u, r.right, r.top + 13 * u), letra, FOSFORO_TENUE, 8.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left, r.top + 13 * u, r.right, r.top + 35 * u), letra, FOSFORO, 19f, peso = FontWeight.Bold, minimo = 9f)
        val led = Rect(r.left + 6 * u, r.bottom - 10 * u, r.right - 6 * u, r.bottom - 5 * u)
        if (e.tono != null) {
            drawRect(e.tono.copy(alpha = 0.3f), led.inflate(1.5f * u).topLeft, led.inflate(1.5f * u).size)
            drawRect(e.tono, led.topLeft, led.size)
        } else drawLine(FOSFORO_TENUE.copy(alpha = 0.6f), Offset(led.left + 4 * u, led.center.y), Offset(led.right - 4 * u, led.center.y), 1.2f * u)
    }

    /** El mes como encabezado del registro en la terminal: «# octubre · 2026», una raya de guiones y el cursor. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(FONDO)
        drawRect(PANTALLA, Offset(0f, 4 * u), Size(w, h - 8 * u))
        drawLine(FOSFORO_TENUE.copy(alpha = 0.6f), Offset(0f, 4 * u), Offset(w, 4 * u), 0.8f * u)
        drawLine(FOSFORO_TENUE.copy(alpha = 0.6f), Offset(0f, h - 4 * u), Offset(w, h - 4 * u), 0.8f * u)
        val fin = renglonCon(medidor, "# $texto", 12 * u, h / 2, w - 40 * u, letra, FOSFORO, 13f, peso = FontWeight.Bold, minimo = 7f)
        drawRect(FOSFORO, Offset(fin + 5 * u, h / 2 - 6 * u), Size(6.5f * u, 12 * u))
        var x = fin + 18 * u
        while (x < w - 12 * u) { drawLine(FOSFORO_TENUE.copy(alpha = 0.7f), Offset(x, h / 2), Offset(x + 4 * u, h / 2), 1 * u); x += 7 * u }
    }

    // ---------------------------------------------------------------- las cartas

    /** La funda antiestática, el cian del holograma de seguridad, el magenta de un cable y el rojo del candado. */
    private val FUNDA = Color(0xFF3A4048)
    private val FUNDA_LUZ = Color(0xFF6E7883)
    private val FUNDA_OSC = Color(0xFF1C2026)
    private val CIAN = Color(0xFF00E5FF)
    private val MAGENTA = Color(0xFFE040B0)
    private val CANDADO = Color(0xFFFF4A5A)

    /** El color de la escena: el tema solo tiene modo oscuro, así que todo va siempre en la penumbra del escritorio. */
    private fun o(x: Color): Color = t(x, true, false)

    /**
     * El holograma de seguridad: un círculo plateado con el candado y los anillos que cambian de color, el «VOID» que queda
     * si se despega; con [alfa], al caer al sellar.
     */
    private fun DrawScope.holograma(c: Offset, r: Float, medidor: TextMeasurer, alfa: Float) {
        val u = density
        fun k(x: Color) = x.copy(alpha = x.alpha * alfa)
        drawCircle(Color.Black.copy(alpha = 0.4f * alfa), r, c + Offset(0.6f * u, 1 * u))
        drawCircle(k(o(METAL)), r, c)
        listOf(CIAN, MAGENTA, PILOTO, CIAN).forEachIndexed { i, col ->
            drawArc(k(col.copy(alpha = 0.55f)), 30f + i * 90f, 70f, false, Offset(c.x - r * 0.82f, c.y - r * 0.82f), Size(r * 1.64f, r * 1.64f), style = Stroke(r * 0.12f))
        }
        drawCircle(k(o(FUNDA_OSC)), r * 0.62f, c)
        // El candado.
        val cw = r * 0.5f; val chh = r * 0.38f
        drawArc(k(CIAN), 180f, 180f, false, Offset(c.x - cw * 0.32f, c.y - chh * 1.05f), Size(cw * 0.64f, chh * 1.1f), style = Stroke(r * 0.08f))
        drawRoundRect(k(CIAN), Offset(c.x - cw / 2, c.y - chh * 0.45f), Size(cw, chh), CornerRadius(r * 0.06f))
        drawCircle(k(o(FUNDA_OSC)), r * 0.06f, Offset(c.x, c.y - chh * 0.45f + chh * 0.45f))
        textoEn(medidor, "VOID", Rect(c.x - r * 0.6f, c.y + r * 0.62f, c.x + r * 0.6f, c.y + r * 0.95f), letra, k(o(FUNDA_OSC)), r * 0.3f / u, minimo = 3f)
    }

    /**
     * La funda antiestática plateada, sellada: por su plástico ahumado se adivina el disquete de 5¼ que lleva dentro; en la
     * solapa doblada, el triángulo de aviso de descarga y el código de barras; abajo, la etiqueta blanca escrita a bolígrafo
     * con a quién va y las fechas. La cierra el holograma de seguridad con su candado cian; las de al futuro, además, una
     * cinta de precinto rayada. Al abrirla, el holograma se rasga, la solapa se dobla hacia atrás y sube la carta en papel
     * continuo de impresora, con sus agujeros de arrastre. Siempre en oscuro (el tema no tiene claro).
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(l + 1.5f * u, top + 2.5f * u), b.size)
        // La cara de atrás, por dentro: plástico oscuro.
        drawRect(o(FUNDA_OSC), b.topLeft, b.size)
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val hondo = H * 0.36f
        val solapa = Path().apply { moveTo(l, top); lineTo(r, top); lineTo(r, top + hondo); lineTo(l, top + hondo); close() }
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            drawPath(solapa, o(mezcla(FUNDA_OSC, FUNDA, 0.4f)))
            drawRect(o(FOSFORO_TENUE).copy(alpha = 0.5f), Offset(l, top + hondo - 3 * u), Size(W, 1.2f * u))
        }
        // La carta: papel continuo con sus agujeros de arrastre.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.86f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 7 * u, 5.5f * u, 9 * u, oscuro)
            var y = h.top + 2.5f * u
            while (y < h.bottom) {
                listOf(h.left + 3.5f * u, h.right - 3.5f * u).forEach { x -> drawCircle(mezcla(e.hoja, e.renglones.copy(alpha = 1f), 0.35f), 1.1f * u, Offset(x, y)) }
                y += 4.2f * u
            }
            drawLine(e.renglones, Offset(h.left + 6.5f * u, h.top), Offset(h.left + 6.5f * u, h.bottom), 0.5f * u)
            drawLine(e.renglones, Offset(h.right - 6.5f * u, h.top), Offset(h.right - 6.5f * u, h.bottom), 0.5f * u)
            drawRect(o(CIAN).copy(alpha = 0.6f), Offset(h.left + 9 * u, h.top + 3 * u), Size(h.width * 0.2f, 1 * u))
        }
        // El frente de la funda: plástico ahumado con el disquete adivinado, los reflejos y la boca con su cierre a presión.
        val frente = Rect(l, top + H * 0.12f, r, bot)
        drawRect(o(FUNDA), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            val dl = minOf(W * 0.62f, H * 0.95f)
            val disco = Rect(cx - dl / 2, frente.top + H * 0.08f, cx + dl / 2, frente.top + H * 0.08f + dl)
            drawRect(o(FUNDA_OSC).copy(alpha = 0.55f), disco.topLeft, disco.size)
            drawCircle(o(FUNDA).copy(alpha = 0.8f), dl * 0.14f, disco.center)
            drawCircle(o(FUNDA_LUZ).copy(alpha = 0.3f), dl * 0.14f, disco.center, style = Stroke(1 * u))
            drawRoundRect(o(FUNDA).copy(alpha = 0.8f), Offset(cx - dl * 0.05f, disco.center.y + dl * 0.2f), Size(dl * 0.1f, dl * 0.26f), CornerRadius(dl * 0.05f))
            for (j in 0 until 5) {
                val x0 = l + W * (0.1f + 0.2f * j) + W * 0.1f * azar(e.semilla, j)
                drawLine(o(FUNDA_LUZ).copy(alpha = 0.22f), Offset(x0, frente.top), Offset(x0 - W * 0.18f, bot), (1.5f + 3f * azar(e.semilla, j + 9)) * u)
            }
            for (j in 0 until 6) { val x0 = l + W * azar(e.semilla, j + 20); drawLine(o(FUNDA_LUZ).copy(alpha = 0.18f), Offset(x0, frente.top + H * azar(e.semilla, j + 30) * 0.5f), Offset(x0 + 6 * u, frente.top + H * (0.2f + azar(e.semilla, j + 40) * 0.6f)), 0.6f * u) }
        }
        drawRect(o(FUNDA_LUZ), Offset(l, frente.top), Size(W, 1.4f * u))
        drawRect(o(FUNDA_OSC), Offset(l, frente.top + 3 * u), Size(W, 1.2f * u))
        drawRect(o(FUNDA_OSC), frente.topLeft, frente.size, style = Stroke(1 * u))
        // La etiqueta blanca, escrita a bolígrafo.
        val et = Rect(l + W * 0.08f, top + H * 0.56f, r - W * 0.08f, top + H * 0.92f)
        drawRect(Color.Black.copy(alpha = 0.35f), et.topLeft + Offset(0.8f * u, 1 * u), et.size)
        drawRect(mezcla(ETIQUETA, Color.Black, 0.1f), et.topLeft, et.size)
        drawRect(o(CIAN).copy(alpha = 0.9f), et.topLeft, Size(et.width, 2 * u))
        for (j in 1..2) drawLine(Color(0xFF9FB7C9).copy(alpha = 0.6f), Offset(et.left + 3 * u, et.top + et.height * j / 2.15f + 1.5f * u), Offset(et.right - 3 * u, et.top + et.height * j / 2.15f + 1.5f * u), 0.5f * u)
        textoEn(medidor, e.linea1, Rect(et.left + 4 * u, et.top + 2 * u, et.right - 4 * u, et.top + et.height * 0.56f), letra, BOLIGRAFO, 11.5f, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(et.left + 4 * u, et.top + et.height * 0.56f, et.right - 4 * u, et.bottom - 1 * u), letra, BOLIGRAFO.copy(alpha = 0.85f), 9f, minimo = 6f)
        // La solapa doblada encima, con el aviso de descarga y el código de barras.
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(l, top + 1.5f * u), Size(W, hondo))
            drawPath(solapa, o(mezcla(FUNDA, FUNDA_LUZ, 0.25f)))
            clipPath(solapa) {
                for (j in 0 until 3) { val x0 = l + W * (0.2f + 0.3f * j); drawLine(o(FUNDA_LUZ).copy(alpha = 0.25f), Offset(x0, top), Offset(x0 - W * 0.1f, top + hondo), 3 * u) }
                var x = l + 3 * u
                while (x < r - 3 * u) { drawCircle(o(FUNDA_OSC), 0.7f * u, Offset(x, top + hondo - 4 * u)); x += 3 * u }
                val tr = Offset(l + W * 0.12f, top + hondo * 0.48f); val tl = hondo * 0.26f
                val tri = Path().apply { moveTo(tr.x, tr.y - tl); lineTo(tr.x + tl, tr.y + tl * 0.75f); lineTo(tr.x - tl, tr.y + tl * 0.75f); close() }
                drawPath(tri, o(AMBAR)); drawPath(tri, o(FUNDA_OSC), style = Stroke(0.7f * u))
                drawLine(o(FUNDA_OSC), Offset(tr.x, tr.y - tl * 0.4f), Offset(tr.x, tr.y + tl * 0.3f), 1 * u)
                drawCircle(o(FUNDA_OSC), 0.7f * u, Offset(tr.x, tr.y + tl * 0.52f))
                val cb = Rect(r - W * 0.3f, top + hondo * 0.25f, r - W * 0.07f, top + hondo * 0.62f)
                drawRect(mezcla(ETIQUETA, Color.Black, 0.15f), cb.topLeft, cb.size)
                var bx = cb.left + 2 * u; var j = 0
                while (bx < cb.right - 2 * u) { val g = (0.5f + 1.2f * azar(e.semilla + 5, j)) * u; drawRect(o(FUNDA_OSC), Offset(bx, cb.top + 1.5f * u), Size(g, cb.height - 3 * u)); bx += g + (0.6f + azar(e.semilla + 6, j)) * u; j++ }
            }
            drawRect(o(FUNDA_LUZ).copy(alpha = 0.7f), Offset(l, top + hondo - 1 * u), Size(W, 1 * u))
        }
        // El precinto: una cinta rayada, cian y negra, que corre de lado a lado bajo el holograma.
        val selloC = Offset(cx, top + hondo * k)
        val selloR = H * 0.14f
        if (e.cordon > 0f) {
            val ya = top + hondo; val alto = 7 * u
            val hasta = l + W * e.cordon
            drawRect(Color.Black.copy(alpha = 0.4f), Offset(l, ya - alto / 2 + 1.2f * u), Size(hasta - l, alto))
            drawRect(o(FUNDA_OSC), Offset(l, ya - alto / 2), Size(hasta - l, alto))
            clipRect(l, ya - alto / 2, hasta, ya + alto / 2) {
                var x = l; while (x < hasta) { drawLine(o(CIAN).copy(alpha = 0.85f), Offset(x, ya + alto / 2), Offset(x + alto * 0.7f, ya - alto / 2), alto * 0.32f); x += alto * 1.2f }
            }
        }
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, CIAN, e.brillo)
            selloPartidoCo(Offset(cx, top + hondo), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.25f * e.romper, selloC.y - (top + hondo) - selloR * 0.3f * e.romper),
                Offset(selloR * 0.2f * e.romper, selloR * 0.3f * e.romper), e.semilla) { holograma(Offset(cx, top + hondo), selloR, medidor, e.alfaSello) }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            selloPartidoCo(Offset(cx, top + hondo), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.2f, selloR * 0.3f), e.semilla, conArriba = false) {
                holograma(Offset(cx, top + hondo), selloR, medidor, e.alfaSello)
            }
        }
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.235f, 0.84f, 0.39f)

    override val altoBuzon: Dp = 206.dp

    /**
     * El chasis de cuchillas del servidor de correo, en su rack: arriba, el visor de fósforo con las tres cuentas (RX las que
     * llegaron y se pueden abrir, LOCK las selladas, READ las leídas); debajo, diez ranuras con una cuchilla por carta, de
     * pie, con su número, sus pilotos y su asa: las listas con el piloto cian encendido y su halo (el primer degradado), las
     * selladas con el candado rojo y la brida, y las leídas medio sacadas, con el papel continuo saliendo por arriba. Las
     * ranuras vacías llevan su tapa ciega con rejilla. Abajo, el panel de conexiones con sus cables. Detrás, la sala a oscuras
     * con el resplandor del visor (el segundo degradado). Siempre en oscuro.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(o(FONDO))
        // Los postes del rack, con sus agujeros cuadrados.
        val poste = 10 * u
        listOf(0f, w - poste).forEach { x ->
            drawRect(o(PLASTICO_OSC), Offset(x, 0f), Size(poste, h))
            var y = 4 * u; while (y < h) { drawRect(Color.Black, Offset(x + poste * 0.35f, y), Size(poste * 0.3f, 2.4f * u)); y += 6 * u }
            drawLine(o(PLASTICO_LUZ).copy(alpha = 0.5f), Offset(x + 0.5f * u, 0f), Offset(x + 0.5f * u, h), 1 * u)
        }
        val il = poste + 3 * u; val ir = w - poste - 3 * u
        // El visor de fósforo.
        val visor = Rect(il, 6 * u, ir, 42 * u)
        drawRect(o(PLASTICO), visor.topLeft, visor.size)
        bisel(visor, o(PLASTICO_LUZ).copy(alpha = 0.6f), Color.Black.copy(alpha = 0.6f), 1.5f * u)
        val pant = Rect(visor.left + 8 * u, visor.top + 6 * u, visor.right - 30 * u, visor.bottom - 6 * u)
        drawRect(o(PANTALLA), pant.topLeft, pant.size)
        mancha(pant.center, pant.width * 0.6f, FOSFORO.copy(alpha = 0.16f))
        val cuentas = listOf("RX" to e.listas, "LOCK" to e.selladas, "READ" to e.abiertas)
        val cw = pant.width / 3
        cuentas.forEachIndexed { i, (txt, n) ->
            val x0 = pant.left + i * cw
            if (i > 0) drawLine(FOSFORO_TENUE, Offset(x0, pant.top + 3 * u), Offset(x0, pant.bottom - 3 * u), 0.7f * u)
            val col = if (i == 0 && n > 0) CIAN else FOSFORO
            textoEn(medidor, txt, Rect(x0 + 3 * u, pant.top, x0 + cw * 0.5f, pant.bottom), letra, col.copy(alpha = 0.75f), 9f, minimo = 5f)
            textoEn(medidor, "%02d".format(n.coerceAtMost(99)), Rect(x0 + cw * 0.45f, pant.top, x0 + cw - 3 * u, pant.bottom), letra, col, 17f, minimo = 7f)
        }
        listOf(PILOTO, AMBAR).forEachIndexed { i, col -> drawCircle(col, 2.2f * u, Offset(visor.right - 18 * u + i * 9 * u, visor.center.y)) }
        // El chasis, con sus diez ranuras.
        val chasis = Rect(il, visor.bottom + 4 * u, ir, h - 30 * u)
        drawRect(o(PLASTICO_OSC), chasis.topLeft, chasis.size)
        bisel(chasis, o(PLASTICO_LUZ).copy(alpha = 0.5f), Color.Black.copy(alpha = 0.6f), 1.5f * u)
        val n = 10; val gap = 3 * u
        val sw = (chasis.width - 8 * u - gap * (n - 1)) / n
        val cartas = cartasBuzonCo(e, n)
        for (i in 0 until n) {
            val x0 = chasis.left + 4 * u + i * (sw + gap)
            val ranura = Rect(x0, chasis.top + 20 * u, x0 + sw, chasis.bottom - 4 * u)
            drawRect(Color.Black, ranura.topLeft, ranura.size)
            drawRect(Color.Black.copy(alpha = 0.5f), Offset(ranura.left, chasis.top + 4 * u), Size(sw, 14 * u))
            val tipo = cartas.getOrNull(i)
            if (tipo == null) {
                drawRect(o(PLASTICO), ranura.topLeft, ranura.size)
                var y = ranura.top + 6 * u
                while (y < ranura.bottom - 6 * u) { drawLine(Color.Black.copy(alpha = 0.7f), Offset(ranura.left + 3 * u, y), Offset(ranura.right - 3 * u, y), 1.2f * u); y += 3.4f * u }
                continue
            }
            val sube = if (tipo == 2) 7 * u else 0f
            val cu = Rect(ranura.left, ranura.top - sube, ranura.right, ranura.bottom - sube)
            if (tipo == 2) {
                // El papel continuo que sale por arriba de la cuchilla leída.
                val pp = Rect(cu.left + sw * 0.15f, cu.top - 10 * u, cu.right - sw * 0.15f, cu.top + 2 * u)
                drawRect(PAPEL_CONTINUO.copy(alpha = 0.8f), pp.topLeft, pp.size)
                var yy = pp.top + 2 * u; while (yy < pp.bottom) { drawCircle(o(FUNDA_OSC), 0.7f * u, Offset(pp.left + 1.6f * u, yy)); drawCircle(o(FUNDA_OSC), 0.7f * u, Offset(pp.right - 1.6f * u, yy)); yy += 3 * u }
                for (j in 0 until 2) drawLine(o(FOSFORO_TENUE), Offset(pp.left + 3.5f * u, pp.top + 3 * u + j * 3 * u), Offset(pp.right - 3.5f * u, pp.top + 3 * u + j * 3 * u), 0.6f * u)
            }
            if (tipo == 0) mancha(Offset(cu.center.x, cu.top + cu.height * 0.3f), sw * 1.3f, CIAN.copy(alpha = 0.4f))
            drawRect(o(if (tipo == 2) PLASTICO else FUNDA), cu.topLeft, cu.size)
            bisel(cu, o(FUNDA_LUZ).copy(alpha = 0.6f), Color.Black.copy(alpha = 0.5f), 1 * u)
            // El asa, la etiqueta con el número y los pilotos.
            drawRect(o(METAL), Offset(cu.left + sw * 0.2f, cu.bottom - 9 * u), Size(sw * 0.6f, 3 * u))
            val et = Rect(cu.left + 2 * u, cu.top + 4 * u, cu.right - 2 * u, cu.top + 14 * u)
            drawRect(mezcla(ETIQUETA, Color.Black, 0.12f), et.topLeft, et.size)
            textoEn(medidor, "%02d".format(i + 1), et, letra, BOLIGRAFO, 8f, minimo = 5f)
            val p0 = Offset(cu.center.x - sw * 0.18f, cu.top + 20 * u); val p1 = Offset(cu.center.x + sw * 0.18f, cu.top + 20 * u)
            drawCircle(if (tipo == 0) CIAN else o(FOSFORO_TENUE), 1.8f * u, p0)
            drawCircle(when (tipo) { 1 -> CANDADO; 0 -> PILOTO; else -> o(PLASTICO_OSC) }, 1.8f * u, p1)
            for (j in 0 until 4) drawLine(Color.Black.copy(alpha = 0.6f), Offset(cu.left + 3 * u, cu.top + 27 * u + j * 3.2f * u), Offset(cu.right - 3 * u, cu.top + 27 * u + j * 3.2f * u), 1.1f * u)
            val cc = Offset(cu.center.x, cu.top + cu.height * 0.68f)
            when (tipo) {
                0 -> { drawCircle(CIAN, sw * 0.22f, cc, style = Stroke(1.4f * u)); drawLine(CIAN, cc - Offset(0f, sw * 0.12f), cc + Offset(0f, sw * 0.04f), 1.4f * u, StrokeCap.Round) }
                1 -> {
                    // El candado y la brida que abraza la cuchilla.
                    drawArc(CANDADO, 180f, 180f, false, Offset(cc.x - sw * 0.12f, cc.y - sw * 0.24f), Size(sw * 0.24f, sw * 0.26f), style = Stroke(1.3f * u))
                    drawRect(CANDADO, Offset(cc.x - sw * 0.18f, cc.y - sw * 0.1f), Size(sw * 0.36f, sw * 0.26f))
                    drawRect(o(FUNDA_OSC), Offset(cu.left - 1 * u, cc.y + sw * 0.3f), Size(sw + 2 * u, 2.6f * u))
                    drawRect(o(FUNDA_OSC), Offset(cu.right - 3 * u, cc.y + sw * 0.25f), Size(4 * u, 4 * u))
                }
                else -> drawRect(o(FOSFORO_TENUE), Offset(cc.x - sw * 0.2f, cc.y - 1 * u), Size(sw * 0.4f, 2 * u))
            }
        }
        // El panel de conexiones, con sus cables.
        val panel = Rect(il, h - 26 * u, ir, h - 6 * u)
        drawRect(o(PLASTICO), panel.topLeft, panel.size)
        bisel(panel, o(PLASTICO_LUZ).copy(alpha = 0.5f), Color.Black.copy(alpha = 0.6f), 1 * u)
        val puertos = 16
        val pw = (panel.width - 16 * u) / puertos
        for (j in 0 until puertos) {
            val p = Offset(panel.left + 8 * u + pw * (j + 0.5f), panel.center.y)
            drawRect(Color.Black, Offset(p.x - pw * 0.3f, p.y - 3 * u), Size(pw * 0.6f, 6 * u))
            if (azar(97, j) > 0.4f) {
                val col = listOf(CIAN, MAGENTA, o(METAL), AMBAR)[(azar(98, j) * 4).toInt().coerceAtMost(3)]
                drawLine(col.copy(alpha = 0.85f), p, Offset(p.x + (azar(99, j) - 0.5f) * 20 * u, h + 2 * u), 2.2f * u)
            }
        }
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
 * La tapa es ese libro mayor tumbado, con la gran placa remachada del «REGISTRO DE PATENTES», su tarjeta crema, la fecha
 * grabada y el engrane junto al canto; al abrirlo, la tapa gira y deja ver las columnas de debe y haber.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa: Rect = Rect(0.255f, 0.26f, 0.775f, 0.615f)
    override val letraTapa: FontFamily = LetraExtra.caldera
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = HIERRO_OSC
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La tarjeta de la placa, donde va el extracto: crema, hundida en oscuro y entibiada de noche, siempre clara. */
    private fun tarjetaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = CARA
        if (oscuro) r = mezcla(r, Color(0xFF0D0B09), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.14f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val cuero = c(CUEROS[(e.semilla and 0x7fffffff) % CUEROS.size])
        val cu = Rect(w * 0.02f, h * 0.05f, w * 0.965f, h * 0.93f)
        val bisagra = cu.left + w * 0.075f
        // De noche, la lámpara de gas alumbra la mesa por detrás del libro (el único degradado).
        if (e.noche) mancha(Offset(w * 0.95f, 0f), w * 0.55f, GAS.copy(alpha = 0.28f))
        drawRoundRect(Color.Black.copy(alpha = 0.22f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(3 * u))
        // El canto de las hojas del libro mayor, con sus pliegos.
        drawRect(c(CARA), Offset(cu.left + 3 * u, cu.top + 3 * u), Size(cu.width, cu.height))
        for (k in 1..4) {
            drawLine(c(LATON_OSC).copy(alpha = 0.35f), Offset(cu.left + 8 * u, cu.bottom + k * 0.6f * u), Offset(cu.right + 2 * u, cu.bottom + k * 0.6f * u), 0.4f * u)
            drawLine(c(LATON_OSC).copy(alpha = 0.35f), Offset(cu.right + k * 0.6f * u, cu.top + 6 * u), Offset(cu.right + k * 0.6f * u, cu.bottom + 2 * u), 0.4f * u)
        }
        // La cinta de registro, roja, que sale por el pie.
        val xc = w * 0.32f; val cw = w * 0.022f; val lc = cu.bottom + h * 0.06f
        drawPath(Path().apply { moveTo(xc, cu.bottom); lineTo(xc + cw, cu.bottom); lineTo(xc + cw, lc); lineTo(xc + cw / 2, lc - cw * 0.6f); lineTo(xc, lc); close() }, c(ROJO))
        // Por dentro (se ve al abrirse): la hoja del libro mayor, con sus columnas de debe y haber.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(CARA), hoja.topLeft, hoja.size)
            drawRect(c(LATON).copy(alpha = 0.25f), hoja.topLeft, Size(hoja.width, hoja.height * 0.12f))
            var ly = hoja.top + hoja.height * 0.16f
            while (ly < hoja.bottom - 3 * u) { drawLine(c(HIERRO_LUZ).copy(alpha = 0.3f), Offset(hoja.left, ly), Offset(hoja.right, ly), 0.5f * u); ly += 7 * u }
            listOf(0.14f, 0.7f, 0.85f).forEach { f ->
                val x = hoja.left + hoja.width * f
                drawLine(c(ROJO).copy(alpha = 0.5f), Offset(x, hoja.top), Offset(x, hoja.bottom), 0.6f * u)
                drawLine(c(ROJO).copy(alpha = 0.5f), Offset(x + 2 * u, hoja.top), Offset(x + 2 * u, hoja.bottom), 0.6f * u)
            }
        }
        // La tapa de cuero, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(cuero, cu.topLeft, cu.size, CornerRadius(3 * u))
            // El grano del cuero y sus bordes gastados.
            for (j in 0 until 140) drawCircle(Color.Black.copy(alpha = 0.1f), (0.5f + 0.6f * azar(e.semilla, 300 + j)) * u, Offset(cu.left + cu.width * azar(e.semilla, 100 + j), cu.top + cu.height * azar(e.semilla, 200 + j)))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cu.right - 4 * u, cu.top), Size(4 * u, cu.height))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cu.left, cu.bottom - 4 * u), Size(cu.width, 4 * u))
            drawRect(Color.White.copy(alpha = 0.06f), Offset(cu.left, cu.top), Size(cu.width, 3 * u))
            // Las filas de remaches, arriba y abajo.
            val k = cu.height * 0.15f
            var rx = bisagra + 10 * u
            while (rx < cu.right - k * 1.1f) {
                listOf(cu.top + 5 * u, cu.bottom - 5 * u).forEach { ry ->
                    drawCircle(Color.Black.copy(alpha = 0.3f), 1.8f * u, Offset(rx + 0.5f * u, ry + 0.6f * u))
                    drawCircle(c(LATON), 1.6f * u, Offset(rx, ry))
                    drawCircle(c(LATON_LUZ), 0.6f * u, Offset(rx - 0.5f * u, ry - 0.5f * u))
                }
                rx += 12 * u
            }
            // Las cantoneras de latón del canto, con su remache.
            listOf(Offset(cu.right, cu.top) to Offset(-1f, 1f), Offset(cu.right, cu.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
                val esquina = Path().apply {
                    moveTo(o.x, o.y); lineTo(o.x + s.x * k, o.y); quadraticTo(o.x + s.x * k * 0.55f, o.y + s.y * k * 0.55f, o.x, o.y + s.y * k); close()
                }
                drawPath(esquina, c(LATON))
                drawPath(esquina, c(LATON_OSC), style = Stroke(0.8f * u))
                drawCircle(c(LATON_OSC), 1.8f * u, Offset(o.x + s.x * k * 0.28f, o.y + s.y * k * 0.28f))
                drawCircle(c(LATON_LUZ), 0.7f * u, Offset(o.x + s.x * k * 0.28f - 0.5f * u, o.y + s.y * k * 0.28f - 0.5f * u))
            }
            // La placa grande de latón, remachada, con su filete grabado.
            val pl = Rect(w * 0.2f, h * 0.11f, w * 0.83f, h * 0.87f)
            drawRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(1.2f * u, 1.6f * u), pl.size)
            drawRect(c(LATON), pl.topLeft, pl.size)
            drawRect(c(LATON_LUZ), pl.topLeft, Size(pl.width, 1.4f * u))
            drawRect(c(LATON_OSC), Offset(pl.left, pl.bottom - 1.4f * u), Size(pl.width, 1.4f * u))
            drawRect(c(LATON_OSC), pl.topLeft, pl.size, style = Stroke(0.8f * u))
            drawRect(c(LATON_OSC).copy(alpha = 0.7f), pl.deflate(4 * u).topLeft, pl.deflate(4 * u).size, style = Stroke(0.6f * u))
            listOf(Offset(pl.left + 8 * u, pl.top + 8 * u), Offset(pl.right - 8 * u, pl.top + 8 * u), Offset(pl.left + 8 * u, pl.bottom - 8 * u), Offset(pl.right - 8 * u, pl.bottom - 8 * u)).forEach { o ->
                drawCircle(c(LATON_OSC), 2.2f * u, o)
                drawCircle(c(LATON_LUZ), 0.8f * u, Offset(o.x - 0.6f * u, o.y - 0.6f * u))
            }
            // El rótulo grabado, entre dos engranes pequeños.
            val rotulo = Rect(pl.left + 14 * u, h * 0.135f, pl.right - 14 * u, h * 0.215f)
            textoEn(medidor, "REGISTRO DE PATENTES", Rect(rotulo.left + 10 * u, rotulo.top, rotulo.right - 10 * u, rotulo.bottom), LetraExtra.bitter, c(HIERRO_OSC).copy(alpha = 0.85f), 9.5f,
                peso = FontWeight.SemiBold, espaciado = 1.5f, minimo = 6f)
            listOf(rotulo.left + 3 * u, rotulo.right - 3 * u).forEach { x ->
                drawPath(engrane(Offset(x, rotulo.center.y), 3.4f * u, 6), c(LATON_OSC))
                drawCircle(c(LATON), 1.2f * u, Offset(x, rotulo.center.y))
            }
            // La tarjeta crema en su portatarjetas, donde va el extracto.
            val tarjeta = Rect(w * 0.235f, h * 0.235f, w * 0.795f, h * 0.64f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tarjeta.left, tarjeta.top), Size(tarjeta.width, 1.2f * u))
            drawRect(tarjetaTapa(oscuro, e.noche), Offset(tarjeta.left, tarjeta.top + 1.2f * u), Size(tarjeta.width, tarjeta.height - 1.2f * u))
            drawRect(c(LATON_OSC), tarjeta.topLeft, tarjeta.size, style = Stroke(1.2f * u))
            drawRect(c(LATON_LUZ).copy(alpha = 0.6f), Offset(tarjeta.left - 1.6f * u, tarjeta.bottom + 0.6f * u), Size(tarjeta.width + 3.2f * u, 0.8f * u))
            // La fecha grabada (en dos renglones si es larga) y las palabras al pie.
            val (f1, f2) = partirFecha(e.fecha, 36)
            if (f2 == null) {
                textoEn(medidor, f1, Rect(pl.left + 8 * u, h * 0.66f, pl.right - 8 * u, h * 0.755f), letra, c(HIERRO_OSC), 10.5f, minimo = 6f)
            } else {
                textoEn(medidor, f1, Rect(pl.left + 8 * u, h * 0.655f, pl.right - 8 * u, h * 0.72f), letra, c(HIERRO_OSC), 9.5f, minimo = 6f)
                textoEn(medidor, f2, Rect(pl.left + 8 * u, h * 0.72f, pl.right - 8 * u, h * 0.775f), letra, c(HIERRO_OSC).copy(alpha = 0.8f), 8.5f, minimo = 6f)
            }
            drawLine(c(LATON_OSC), Offset(pl.left + pl.width * 0.3f, h * 0.785f), Offset(pl.right - pl.width * 0.3f, h * 0.785f), 0.6f * u)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(pl.left + 14 * u, h * 0.792f, pl.right - 14 * u, h * 0.85f), LetraExtra.bitter, c(HIERRO_OSC).copy(alpha = 0.75f), 8.5f, minimo = 6f)
            }
            // El engrane estampado en dorado, junto al canto; de noche, una luna.
            val ce = Offset(w * 0.9f, cu.center.y)
            val re = minOf(w * 0.04f, h * 0.075f)
            if (e.noche) {
                drawCircle(LUNA, re, ce)
                drawCircle(cuero, re * 0.84f, Offset(ce.x + re * 0.45f, ce.y - re * 0.25f))
                drawCircle(LUNA.copy(alpha = 0.7f), 0.9f * u, Offset(ce.x - re * 0.3f, ce.y + re * 1.7f))
                drawCircle(LUNA.copy(alpha = 0.6f), 0.7f * u, Offset(ce.x + re * 0.6f, ce.y - re * 1.8f))
            } else {
                drawPath(engrane(ce, re, 10), Color.Black.copy(alpha = 0.25f))
                drawPath(engrane(Offset(ce.x - 0.6f * u, ce.y - 0.6f * u), re, 10), c(LATON_LUZ).copy(alpha = 0.75f))
                drawCircle(cuero, re * 0.34f, Offset(ce.x - 0.6f * u, ce.y - 0.6f * u))
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(3 * u))
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo, redondeado, con sus nervios y los casquillos de latón arriba y abajo.
        drawRect(cuero, cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.Black.copy(alpha = 0.32f), cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.White.copy(alpha = 0.07f), Offset(cu.left + (bisagra - cu.left) * 0.35f, cu.top), Size((bisagra - cu.left) * 0.2f, cu.height))
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f ->
            val ny = cu.top + cu.height * f
            drawRect(Color.Black.copy(alpha = 0.32f), Offset(cu.left, ny + 1.8f * u), Size(bisagra - cu.left, 1.4f * u))
            drawRect(Color.White.copy(alpha = 0.14f), Offset(cu.left, ny), Size(bisagra - cu.left, 1.8f * u))
            drawLine(c(LATON).copy(alpha = 0.75f), Offset(cu.left + 2 * u, ny - 1.4f * u), Offset(bisagra - 2 * u, ny - 1.4f * u), 0.6f * u)
        }
        listOf(cu.top to 1f, cu.bottom to -1f).forEach { (y, s) ->
            drawRect(c(LATON), Offset(cu.left, if (s > 0) y else y - h * 0.05f), Size(bisagra - cu.left, h * 0.05f))
            drawCircle(c(LATON_OSC), 1.4f * u, Offset((cu.left + bisagra) / 2, y + s * h * 0.025f))
        }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 1 * u)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 124.dp

    /** Un engrane de latón con su borde, su cubo y el agujero del eje. */
    private fun DrawScope.engraneLaton(c0: Offset, r: Float, dientes: Int, giro: Float, laton: Color, osc: Color, luz: Color, eje: Color) {
        val u = density
        rotate(giro, c0) {
            val g = engrane(c0, r, dientes)
            drawPath(g, laton)
            drawPath(g, osc, style = Stroke(0.8f * u))
            drawCircle(osc.copy(alpha = 0.6f), r * 0.6f, c0, style = Stroke(0.7f * u))
            for (k in 0 until 4) {
                val a = Math.toRadians(45.0 + 90.0 * k)
                drawLine(osc.copy(alpha = 0.5f), c0, Offset(c0.x + r * 0.6f * cos(a).toFloat(), c0.y + r * 0.6f * sin(a).toFloat()), 1.2f * u)
            }
            drawCircle(luz, r * 0.26f, c0)
            drawCircle(eje, r * 0.11f, c0)
        }
    }

    /**
     * La cartela de la memoria de patente, en doble marco de dibujante con remaches en las esquinas: a la izquierda, la celda
     * con dos engranes de latón que engranan; a la derecha, la placa de latón remachada con «Patente n.º …», la fecha
     * grabada y el detalle. De noche, en la celda, la luna entre nubes de vapor y estrellas.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        // La placa conserva su brillo en modo oscuro (para leerse); de noche, se apaga como todo.
        fun pl(x: Color) = if (e.noche) t(x, false, true) else tonoBotonCon(x, oscuro && !p.oscura, false)
        val m = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(p.tinta.copy(alpha = 0.7f), m.topLeft, m.size, style = Stroke(1.6f * u))
        val inner = m.deflate(3.5f * u)
        drawRect(p.tinta.copy(alpha = 0.35f), inner.topLeft, inner.size, style = Stroke(0.8f * u))
        listOf(m.topLeft, Offset(m.right, m.top), Offset(m.left, m.bottom), m.bottomRight).forEach { o ->
            drawCircle(c(LATON_OSC), 2.6f * u, o); drawCircle(c(LATON), 1.9f * u, o); drawCircle(c(LATON_LUZ), 0.7f * u, o - Offset(0.5f * u, 0.5f * u))
        }
        // La celda del emblema.
        val cel = Rect(inner.left, inner.top, inner.left + 68 * u, inner.bottom)
        drawLine(p.tinta.copy(alpha = 0.5f), Offset(cel.right, inner.top), Offset(cel.right, inner.bottom), 0.9f * u)
        if (!e.noche) {
            val g1 = Offset(cel.left + 27 * u, cel.center.y - 9 * u)
            engraneLaton(Offset(g1.x + 19 * u, g1.y + 25 * u), 11 * u, 7, 12f, c(LATON), c(LATON_OSC), c(LATON_LUZ), p.color)
            engraneLaton(g1, 17 * u, 10, 0f, c(LATON), c(LATON_OSC), c(LATON_LUZ), p.color)
        } else {
            val cl = Offset(cel.center.x + 2 * u, cel.center.y - 12 * u)
            drawCircle(p.acento.copy(alpha = 0.12f), 18 * u, cl)
            crecienteCon(cl, 12 * u, p.acento)
            listOf(Offset(-20f, -16f), Offset(20f, -10f), Offset(-16f, 14f)).forEach { k ->
                drawPath(estrella(Offset(cl.x + k.x * u, cl.y + k.y * u), 2.6f * u, 4, 0.35f), p.acento.copy(alpha = 0.8f))
            }
            // Las nubes de vapor bajo la luna.
            listOf(Offset(-14f, 30f) to 7f, Offset(-4f, 27f) to 9f, Offset(8f, 30f) to 7.5f, Offset(18f, 33f) to 5.5f).forEach { (k, r) ->
                drawCircle(c(VAPOR).copy(alpha = 0.22f), r * u, Offset(cl.x + k.x * u, cl.y + k.y * u))
            }
        }
        // La placa de latón remachada con el nombre de la patente.
        val placa = Rect(cel.right + 8 * u, inner.top + 6 * u, inner.right - 7 * u, inner.top + 27 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.2f), placa.topLeft + Offset(0.8f * u, 1.3f * u), placa.size, CornerRadius(2 * u))
        drawRoundRect(Brush.verticalGradient(listOf(pl(LATON_LUZ), pl(LATON), pl(LATON_OSC)), placa.top, placa.bottom), placa.topLeft, placa.size, CornerRadius(2 * u))
        drawRoundRect(pl(LATON_OSC), placa.deflate(1.8f * u).topLeft, placa.deflate(1.8f * u).size, CornerRadius(1.4f * u), style = Stroke(0.6f * u))
        listOf(placa.left + 5 * u, placa.right - 5 * u).forEach { x ->
            drawCircle(pl(LATON_OSC), 1.8f * u, Offset(x, placa.center.y)); drawCircle(pl(LATON_LUZ), 0.7f * u, Offset(x - 0.4f * u, placa.center.y - 0.4f * u))
        }
        textoEn(medidor, e.rotulo, Rect(placa.left + 10 * u, placa.top, placa.right - 10 * u, placa.bottom), LetraExtra.caldera,
            if (e.noche) p.tinta else Color(0xFF2A1A08), 11f, minimo = 6.5f)
        fechaCon(medidor, e.fecha, Rect(cel.right + 6 * u, inner.top + 31 * u, inner.right - 6 * u, inner.top + 76 * u), LetraExtra.bitter, p.tinta, 20f)
        drawLine(p.tinta.copy(alpha = 0.3f), Offset(cel.right + 12 * u, inner.top + 79 * u), Offset(inner.right - 12 * u, inner.top + 79 * u), 0.6f * u)
        textoEn(medidor, e.detalle, Rect(cel.right + 6 * u, inner.top + 82 * u, inner.right - 6 * u, inner.bottom - 3 * u), LetraExtra.bitter, p.tenue, 11f, minimo = 6.5f)
    }

    /**
     * La lámina de la patente: el doble marco de dibujante con las marcas de las zonas, la cartela de la oficina abajo a la
     * derecha con sus casillas y su engrane, los arcos de compás de un trazado abajo a la izquierda y una cota en el margen.
     * En modo oscuro, la lámpara de gas arriba a la izquierda alumbra la mesa de dibujo.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m0 = 8 * u; val m1 = 12 * u
        drawRect(p.tinta.copy(alpha = 0.45f), Offset(m0, m0), Size(w - 2 * m0, h - 2 * m0), style = Stroke(1.6f * u))
        drawRect(p.tinta.copy(alpha = 0.22f), Offset(m1, m1), Size(w - 2 * m1, h - 2 * m1), style = Stroke(0.8f * u))
        for (k in 1 until 6) {
            val x = w * k / 6f
            drawLine(p.tinta.copy(alpha = 0.35f), Offset(x, m0), Offset(x, m1), 0.8f * u)
            drawLine(p.tinta.copy(alpha = 0.35f), Offset(x, h - m1), Offset(x, h - m0), 0.8f * u)
        }
        for (k in 1 until 9) {
            val y = h * k / 9f
            drawLine(p.tinta.copy(alpha = 0.35f), Offset(m0, y), Offset(m1, y), 0.8f * u)
            drawLine(p.tinta.copy(alpha = 0.35f), Offset(w - m1, y), Offset(w - m0, y), 0.8f * u)
        }
        // La cartela de la oficina de patentes, con sus casillas.
        val cw = 124 * u; val ch = 46 * u
        val o = Offset(w - m1 - 4 * u - cw, h - m1 - 4 * u - ch)
        drawRect(p.tinta.copy(alpha = 0.32f), o, Size(cw, ch), style = Stroke(0.9f * u))
        drawLine(p.tinta.copy(alpha = 0.25f), o + Offset(cw * 0.4f, 0f), o + Offset(cw * 0.4f, ch), 0.8f * u)
        drawLine(p.tinta.copy(alpha = 0.25f), o + Offset(cw * 0.4f, ch / 2), o + Offset(cw, ch / 2), 0.8f * u)
        drawLine(p.tinta.copy(alpha = 0.25f), o + Offset(cw * 0.72f, ch / 2), o + Offset(cw * 0.72f, ch), 0.8f * u)
        listOf(Offset(0.46f, 0.25f) to 0.44f, Offset(0.46f, 0.72f) to 0.2f, Offset(0.78f, 0.72f) to 0.16f).forEach { (k, l) ->
            drawLine(p.tinta.copy(alpha = 0.16f), o + Offset(cw * k.x, ch * k.y), o + Offset(cw * (k.x + l), ch * k.y), 0.7f * u)
        }
        drawPath(engrane(o + Offset(cw * 0.2f, ch / 2), ch * 0.32f, 9), p.acento.copy(alpha = 0.4f))
        drawCircle(p.color, ch * 0.09f, o + Offset(cw * 0.2f, ch / 2))
        // Los arcos de compás de un trazado, con su centro marcado.
        val cc = Offset(m1 + 34 * u, h - m1 - 64 * u)
        listOf(26f, 40f, 54f).forEachIndexed { i, r ->
            drawArc(p.tinta.copy(alpha = 0.075f), -80f + i * 12f, 120f, false, Offset(cc.x - r * u, cc.y - r * u), Size(2 * r * u, 2 * r * u), style = Stroke(0.8f * u))
        }
        drawLine(p.tinta.copy(alpha = 0.12f), cc - Offset(5 * u, 0f), cc + Offset(5 * u, 0f), 0.7f * u)
        drawLine(p.tinta.copy(alpha = 0.12f), cc - Offset(0f, 5 * u), cc + Offset(0f, 5 * u), 0.7f * u)
        drawLine(p.tinta.copy(alpha = 0.08f), cc, Offset(cc.x + 54 * u * 0.77f, cc.y - 54 * u * 0.64f), 0.7f * u)
        // La cota en el margen izquierdo, con sus flechas.
        val xc = m1 + 7 * u; val ya = h * 0.32f; val yb = h * 0.58f
        drawLine(p.tinta.copy(alpha = 0.2f), Offset(xc, ya), Offset(xc, yb), 0.7f * u)
        listOf(ya to 1f, yb to -1f).forEach { (y, d) ->
            drawPath(Path().apply { moveTo(xc, y); lineTo(xc - 2 * u, y + d * 5 * u); lineTo(xc + 2 * u, y + d * 5 * u); close() }, p.tinta.copy(alpha = 0.25f))
            drawLine(p.tinta.copy(alpha = 0.2f), Offset(xc - 4 * u, y), Offset(xc + 4 * u, y), 0.7f * u)
        }
        if (oscuro && !p.oscura) {
            val gas = Offset(w * 0.05f, h * 0.08f)
            penumbraCon(gas, GAS, 0.19f)
            hazCon(gas, 40f, 22f, h * 0.7f, GAS.copy(alpha = 0.03f))
            hazCon(gas, 62f, 14f, h * 0.8f, GAS.copy(alpha = 0.025f))
        }
    }

    /** La regla de latón con su escala, al pie de la barra, remachada en los extremos. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        val r = Rect(0f, h - 9.5f * u, w, h - 1.5f * u)
        drawRect(Brush.verticalGradient(listOf(c(LATON_LUZ), c(LATON), c(LATON_OSC)), r.top, r.bottom), r.topLeft, r.size)
        var x = 14 * u; var k = 0
        while (x < w - 12 * u) {
            drawLine(c(LATON_OSC), Offset(x, r.top), Offset(x, r.top + if (k % 5 == 0) 4.5f * u else 2.2f * u), 0.7f * u)
            x += 3 * u; k++
        }
        listOf(6 * u, w - 6 * u).forEach { xr -> drawCircle(c(LATON_OSC), 2 * u, Offset(xr, r.center.y)); drawCircle(c(LATON_LUZ), 0.8f * u, Offset(xr - 0.4f * u, r.center.y - 0.4f * u)) }
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 0.8f * u)
    }

    /** Una placa de latón con su bisel y un remache en cada esquina. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(1 * u, 2 * u), Size(w, h), CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(LATON_LUZ), c(LATON), c(LATON_OSC))), Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(c(LATON_OSC), Offset(2.5f * u, 2.5f * u), Size(w - 5 * u, h - 5 * u), CornerRadius(2 * u), style = Stroke(0.7f * u))
        drawLine(c(LATON_LUZ).copy(alpha = 0.8f), Offset(3 * u, 1 * u), Offset(w - 3 * u, 1 * u), 0.8f * u)
        listOf(Offset(5.5f * u, 5.5f * u), Offset(w - 5.5f * u, 5.5f * u), Offset(5.5f * u, h - 5.5f * u), Offset(w - 5.5f * u, h - 5.5f * u)).forEach { o ->
            drawCircle(c(LATON_OSC), 1.9f * u, o); drawCircle(c(LATON_LUZ), 0.7f * u, o - Offset(0.4f * u, 0.4f * u))
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFF2A1A08)

    // ---------------------------------------------------------------- la libreta de momentos

    /** La tela verde del cuaderno de patentes, el azul de las cianotipias, la madera clara del tablero y el celuloide. */
    private val TELA_VERDE = Color(0xFF34473C)
    private val CIANOTIPIA = Color(0xFF2B5C8A)
    private val TABLERO = Color(0xFFC9A877)
    private val TABLERO_VETA = Color(0xFF9C7A4C)
    private val CELULOIDE = Color(0xFFB9D2C2)
    private val CORREA = Color(0xFF5A3A22)

    override val altoLibreta: Dp = 256.dp

    /**
     * El cuaderno de patentes sobre la mesa de dibujo, contra el muro de ladrillo tiznado: el tablero de madera clara con un
     * papel de calco clavado con chinchetas, la regla T de peral apoyada en el canto, la escuadra de celuloide, el compás de
     * puntas abierto y el lápiz; en el muro, la lámpara de gas en su brazo de hierro. El cuaderno, de tela verde de libro mayor
     * con lomo de cuero arriba y cantoneras curvas de latón, engorda con las hojas y deja asomar por la derecha las cianotipias
     * guardadas (una cada 12 hojas, hasta 9). La placa de latón de remate en arco lleva el nombre y la cuenta grabados. Al
     * abrirse, la correa se desabrocha y cae, y la tapa se levanta sobre el lomo de arriba: dentro, la lámina de la patente
     * con su engrane dibujado. En modo oscuro, el taller de noche: solo la lámpara de gas alumbra (un degradado; el otro, el
     * brillo del latón de la placa).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto, 0.34f)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0A0806), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0806), 0.3f) else x
        // El muro de ladrillo con sus juntas y el tizne.
        drawRect(c(LADRILLO_JUNTA))
        var fila = 0; var y = 0f
        while (y < h * 0.5f) {
            var x = if (fila % 2 == 0) -12 * u else 0f
            while (x < w) {
                drawRect(c(mezcla(LADRILLO, Color.Black, 0.12f * azar(91, fila * 40 + (x / u).toInt()))), Offset(x + 1 * u, y + 1 * u), Size(22 * u, 8 * u))
                x += 24 * u
            }
            y += 10 * u; fila++
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, 0f), Size(w, 14 * u))
        // La lámpara de gas en su brazo, a la izquierda.
        val gas = Offset(w * 0.1f, h * 0.17f)
        drawLine(c(HIERRO), Offset(0f, gas.y + 16 * u), Offset(gas.x, gas.y + 16 * u), 2.4f * u)
        drawLine(c(HIERRO), Offset(gas.x, gas.y + 16 * u), Offset(gas.x, gas.y + 8 * u), 2.4f * u)
        drawRect(c(LATON), Offset(gas.x - 5 * u, gas.y + 6 * u), Size(10 * u, 3 * u))
        val tubo = Path().apply { moveTo(gas.x - 6 * u, gas.y + 6 * u); quadraticTo(gas.x - 11 * u, gas.y - 6 * u, gas.x - 5 * u, gas.y - 14 * u)
            lineTo(gas.x + 5 * u, gas.y - 14 * u); quadraticTo(gas.x + 11 * u, gas.y - 6 * u, gas.x + 6 * u, gas.y + 6 * u); close() }
        drawPath(tubo, if (oscuro) GAS.copy(alpha = 0.55f) else c(VAPOR).copy(alpha = 0.55f))
        drawPath(tubo, c(LATON_OSC), style = Stroke(0.8f * u))
        drawPath(Path().apply { moveTo(gas.x, gas.y + 3 * u); quadraticTo(gas.x - 3.5f * u, gas.y - 2 * u, gas.x, gas.y - 7 * u); quadraticTo(gas.x + 3.5f * u, gas.y - 2 * u, gas.x, gas.y + 3 * u) },
            if (oscuro) GAS else c(GAS).copy(alpha = 0.5f))
        drawRect(c(HIERRO), Offset(gas.x - 6 * u, gas.y - 17 * u), Size(12 * u, 3 * u))
        // La mesa de dibujo: el tablero inclinado de madera clara, con su listón de canto.
        val tab = Rect(-w * 0.02f, h * 0.36f, w * 1.02f, h * 1.02f)
        val forma = Path().apply { moveTo(tab.left + w * 0.06f, tab.top); lineTo(tab.right - w * 0.06f, tab.top); lineTo(tab.right, tab.bottom); lineTo(tab.left, tab.bottom); close() }
        drawPath(forma, c(TABLERO))
        clipPath(forma) { vetas(tab, c(TABLERO_VETA).copy(alpha = 0.5f), 93, densidad = 0.8f) }
        drawLine(c(mezcla(TABLERO, Color.Black, 0.35f)), Offset(tab.left + w * 0.06f, tab.top), Offset(tab.right - w * 0.06f, tab.top), 3 * u)
        // El papel de calco clavado con chinchetas, debajo del cuaderno.
        val plano = Rect(w * 0.1f, h * 0.42f, w * 0.86f, h * 0.98f)
        rotate(2.5f, plano.center) {
            drawRect(Color.Black.copy(alpha = 0.15f), plano.topLeft + Offset(2 * u, 2 * u), plano.size)
            drawRect(c(mezcla(CARA, CELULOIDE, 0.25f)), plano.topLeft, plano.size)
            var gx = plano.left + 10 * u
            while (gx < plano.right) { drawLine(c(CIANOTIPIA).copy(alpha = 0.12f), Offset(gx, plano.top), Offset(gx, plano.bottom), 0.5f * u); gx += 10 * u }
            var gy = plano.top + 10 * u
            while (gy < plano.bottom) { drawLine(c(CIANOTIPIA).copy(alpha = 0.12f), Offset(plano.left, gy), Offset(plano.right, gy), 0.5f * u); gy += 10 * u }
            drawPath(engrane(Offset(plano.left + 26 * u, plano.bottom - 28 * u), 18 * u, 12), c(HIERRO).copy(alpha = 0.45f), style = Stroke(0.8f * u))
            drawCircle(c(HIERRO).copy(alpha = 0.45f), 5 * u, Offset(plano.left + 26 * u, plano.bottom - 28 * u), style = Stroke(0.8f * u))
            listOf(plano.topLeft, Offset(plano.right, plano.top)).forEach { p -> drawCircle(c(LATON), 3 * u, p + Offset(if (p.x < w / 2) 5 * u else -5 * u, 5 * u)) }
        }
        // La regla T: el cabezal contra el canto izquierdo y la hoja de peral cruzando el tablero.
        val reglaY = h * 0.47f
        rotate(-1f, Offset(w * 0.04f, reglaY)) {
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(w * 0.06f, reglaY + 3 * u), Size(w * 0.9f, 9 * u))
            drawRect(c(mezcla(TABLON, TABLERO, 0.4f)), Offset(w * 0.06f, reglaY), Size(w * 0.9f, 9 * u))
            var mx = w * 0.08f
            var j = 0
            while (mx < w * 0.95f) { drawLine(c(HIERRO).copy(alpha = 0.6f), Offset(mx, reglaY), Offset(mx, reglaY + (if (j % 5 == 0) 4 else 2) * u), 0.6f * u); mx += 4 * u; j++ }
            drawRect(c(TABLON), Offset(w * 0.025f, reglaY - 10 * u), Size(10 * u, 29 * u))
            drawCircle(c(LATON), 1.6f * u, Offset(w * 0.025f + 5 * u, reglaY - 3 * u)); drawCircle(c(LATON), 1.6f * u, Offset(w * 0.025f + 5 * u, reglaY + 12 * u))
        }
        // La escuadra de celuloide, a la izquierda, y el lápiz.
        val esq = Path().apply { moveTo(w * 0.02f, h * 0.62f); lineTo(w * 0.02f, h * 0.97f); lineTo(w * 0.2f, h * 0.97f); close() }
        drawPath(esq, c(CELULOIDE).copy(alpha = 0.55f))
        drawPath(esq, c(mezcla(CELULOIDE, Color.Black, 0.4f)), style = Stroke(1 * u))
        drawPath(Path().apply { moveTo(w * 0.045f, h * 0.76f); lineTo(w * 0.045f, h * 0.93f); lineTo(w * 0.13f, h * 0.93f); close() }, c(TABLERO).copy(alpha = 0.5f))
        rotate(-28f, Offset(w * 0.9f, h * 0.9f)) {
            drawRect(c(Color(0xFF2E5A3A)), Offset(w * 0.9f - 22 * u, h * 0.9f - 2 * u), Size(40 * u, 4 * u))
            drawPath(Path().apply { moveTo(w * 0.9f + 18 * u, h * 0.9f - 2 * u); lineTo(w * 0.9f + 25 * u, h * 0.9f); lineTo(w * 0.9f + 18 * u, h * 0.9f + 2 * u); close() }, c(TABLERO_VETA))
            drawRect(c(LATON), Offset(w * 0.9f - 22 * u, h * 0.9f - 2 * u), Size(4 * u, 4 * u))
        }
        // El compás de puntas, abierto, a la derecha.
        val bis = Offset(w * 0.9f, h * 0.55f)
        listOf(Offset(w * 0.85f, h * 0.8f), Offset(w * 0.97f, h * 0.78f)).forEach { p ->
            drawLine(c(HIERRO_LUZ), bis, p, 2 * u)
            drawLine(c(HIERRO), Offset(p.x + (bis.x - p.x) * 0.12f, p.y + (bis.y - p.y) * 0.12f), p, 1 * u)
        }
        drawCircle(c(LATON), 4 * u, bis); drawCircle(c(LATON_OSC), 1.6f * u, bis)
        drawLine(c(LATON), bis, Offset(bis.x, bis.y - 8 * u), 2 * u)
        // La luz de la lámpara de gas.
        if (oscuro) drawRect(Brush.radialGradient(listOf(GAS.copy(alpha = 0.32f), GAS.copy(alpha = 0.1f), Color.Transparent), gas, w * 0.85f))
        // El cuaderno de patentes.
        val ancho = w * 0.56f; val alto = minOf(ancho * 0.7f, h * 0.52f)
        val r = Rect(w * 0.2f, h * 0.36f, w * 0.2f + ancho, h * 0.36f + alto)
        val g = (3f + 12f * lleno) * u
        val lomo = r.top + alto * 0.1f
        rotate(-2f, r.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.22f), r.topLeft + Offset(5 * u, 5 * u + g), r.size)
            // Las cianotipias guardadas, que asoman por la derecha.
            val planos = (1 + e.hojas / 12).coerceAtMost(9)
            for (k in 0 until planos) {
                val py = r.top + alto * (0.16f + 0.085f * k)
                val sale = (10f + 10f * azar(e.semilla, 70 + k)) * u
                rotate(-4f + 8f * azar(e.semilla, 80 + k), Offset(r.right, py)) {
                    drawRect(c(CIANOTIPIA), Offset(r.right - 30 * u, py), Size(30 * u + sale, alto * 0.12f))
                    drawLine(Color.White.copy(alpha = 0.5f), Offset(r.right + 2 * u, py + alto * 0.06f), Offset(r.right + sale - 3 * u, py + alto * 0.06f), 0.6f * u)
                    drawCircle(Color.White.copy(alpha = 0.45f), alto * 0.03f, Offset(r.right + sale * 0.6f, py + alto * 0.06f), style = Stroke(0.6f * u))
                }
            }
            drawRect(c(mezcla(TELA_VERDE, Color.Black, 0.3f)), r.topLeft + Offset(0f, g), r.size)
            cantoLibreta(r, g * 0.85f, c(CARA), c(LATON_OSC).copy(alpha = 0.5f))
            // Por dentro, al levantar la tapa: la lámina de la patente.
            if (abre > 0.01f) {
                val hoja = Rect(r.left, lomo, r.right, r.bottom)
                drawRect(cp(CARA), hoja.topLeft, hoja.size)
                drawRect(c(HIERRO).copy(alpha = 0.6f), hoja.deflate(6 * u).topLeft, hoja.deflate(6 * u).size, style = Stroke(0.8f * u))
                val ce = Offset(hoja.left + hoja.width * 0.36f, hoja.top + hoja.height * 0.52f)
                val rr = hoja.height * 0.28f
                drawPath(engrane(ce, rr, 14), c(HIERRO).copy(alpha = 0.7f), style = Stroke(1 * u))
                drawCircle(c(HIERRO).copy(alpha = 0.7f), rr * 0.35f, ce, style = Stroke(1 * u))
                drawPath(engrane(Offset(ce.x + rr * 1.7f, ce.y - rr * 0.3f), rr * 0.7f, 10), c(HIERRO).copy(alpha = 0.6f), style = Stroke(1 * u))
                drawLine(c(ROJO).copy(alpha = 0.6f), Offset(ce.x - rr * 1.3f, ce.y), Offset(ce.x + rr * 2.6f, ce.y - rr * 0.3f), 0.6f * u)
                textoEn(medidor, "Fig. 1", Rect(hoja.right - 60 * u, hoja.bottom - 22 * u, hoja.right - 10 * u, hoja.bottom - 9 * u), letra, c(HIERRO), 9f)
            }
            // La tapa de tela verde, que se levanta sobre el lomo de arriba.
            val sy = 1f - abre
            if (sy > 0.02f) scale(1f, sy, Offset(r.center.x, lomo)) {
                drawRoundRect(c(TELA_VERDE), r.topLeft, r.size, CornerRadius(3 * u))
                var ty = r.top
                while (ty < r.bottom) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(r.left, ty), Offset(r.right, ty), 0.5f * u); ty += 2.4f * u }
                var tx = r.left
                while (tx < r.right) { drawLine(Color.White.copy(alpha = 0.03f), Offset(tx, r.top), Offset(tx, r.bottom), 0.5f * u); tx += 2.4f * u }
                drawRect(c(LATON).copy(alpha = 0.6f), r.deflate(7 * u).topLeft, r.deflate(7 * u).size, style = Stroke(0.8f * u))
                // Las cantoneras curvas de abajo, con su remache.
                val e0 = ancho * 0.12f
                listOf(Offset(r.left, r.bottom) to 1f, Offset(r.right, r.bottom) to -1f).forEach { (p, s) ->
                    drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x + s * e0, p.y); quadraticTo(p.x + s * e0 * 0.5f, p.y - e0 * 0.5f, p.x, p.y - e0); close() }, c(LATON))
                    drawCircle(c(LATON_OSC), 1.5f * u, Offset(p.x + s * e0 * 0.26f, p.y - e0 * 0.26f))
                }
                // La placa de latón con su remate en arco: el nombre y la cuenta grabados.
                val pl = Rect(r.left + ancho * 0.18f, r.top + alto * 0.24f, r.right - ancho * 0.18f, r.top + alto * 0.66f)
                val placa = Path().apply { moveTo(pl.left, pl.bottom); lineTo(pl.left, pl.top + pl.height * 0.22f); quadraticTo(pl.center.x, pl.top - pl.height * 0.22f, pl.right, pl.top + pl.height * 0.22f); lineTo(pl.right, pl.bottom); close() }
                translate(1.2f * u, 1.8f * u) { drawPath(placa, Color.Black.copy(alpha = 0.3f)) }
                drawPath(placa, Brush.verticalGradient(listOf(cp(LATON_LUZ), cp(LATON), cp(LATON_OSC)), pl.top, pl.bottom))
                drawPath(placa, c(LATON_OSC), style = Stroke(1 * u))
                listOf(Offset(pl.left + 5 * u, pl.bottom - 5 * u), Offset(pl.right - 5 * u, pl.bottom - 5 * u), Offset(pl.left + 5 * u, pl.top + pl.height * 0.28f), Offset(pl.right - 5 * u, pl.top + pl.height * 0.28f))
                    .forEach { drawCircle(c(LATON_OSC), 1.6f * u, it) }
                textoEn(medidor, e.nombre, Rect(pl.left + 12 * u, pl.top + pl.height * 0.18f, pl.right - 12 * u, pl.top + pl.height * 0.6f), letra, c(HIERRO_OSC), 14f, peso = FontWeight.Bold, minimo = 7.5f)
                drawLine(c(HIERRO_OSC).copy(alpha = 0.5f), Offset(pl.left + 16 * u, pl.top + pl.height * 0.63f), Offset(pl.right - 16 * u, pl.top + pl.height * 0.63f), 0.7f * u)
                textoEn(medidor, e.cuenta, Rect(pl.left + 12 * u, pl.top + pl.height * 0.66f, pl.right - 12 * u, pl.bottom - 4 * u), letra, c(HIERRO_OSC).copy(alpha = 0.85f), 9.5f, minimo = 6f)
                // El engrane estampado en seco, abajo.
                val eg = Offset(r.center.x, r.top + alto * 0.82f)
                drawPath(engrane(eg + Offset(0.8f * u, 0.8f * u), alto * 0.09f, 12), Color.Black.copy(alpha = 0.22f))
                drawPath(engrane(eg, alto * 0.09f, 12), c(mezcla(TELA_VERDE, Color.White, 0.08f)))
                drawCircle(c(TELA_VERDE), alto * 0.035f, eg)
                if (abre > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * abre), r.topLeft, r.size, CornerRadius(3 * u))
            }
            if (abre > 0.01f && sy > 0.02f) {
                val borde = lomo + (r.bottom - lomo) * sy
                drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.left, borde), Size(r.width, 14 * u * sin(PI.toFloat() * abre)))
            }
            // El lomo de cuero, arriba, con sus nervios.
            drawRect(c(mezcla(ROJO, CORREA, 0.5f)), r.topLeft, Size(r.width, lomo - r.top))
            listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + ancho * f - 2 * u, r.top), Size(4 * u, lomo - r.top)) }
            drawLine(Color.White.copy(alpha = 0.15f), Offset(r.left, r.top + 1 * u), Offset(r.right, r.top + 1 * u), 0.8f * u)
            // La correa de cuero con su hebilla: al abrir se desabrocha y cae.
            val queda = 1f - suelta
            if (queda > 0f) {
                val cx = r.left + ancho * 0.84f
                val baja = suelta * alto * 0.5f
                drawRect(Color.Black.copy(alpha = 0.25f * queda), Offset(cx + 2 * u, r.top + alto * 0.62f + baja), Size(12 * u, alto * 0.38f + g))
                drawRect(c(CORREA).copy(alpha = queda), Offset(cx, r.top + alto * 0.6f + baja), Size(12 * u, alto * 0.4f + g + 2 * u))
                drawLine(Color.White.copy(alpha = 0.12f * queda), Offset(cx + 2 * u, r.top + alto * 0.6f + baja), Offset(cx + 2 * u, r.bottom + g), 0.6f * u)
                val heb = Rect(cx - 3 * u, r.top + alto * 0.58f + baja, cx + 15 * u, r.top + alto * 0.7f + baja)
                drawRect(c(LATON).copy(alpha = queda), heb.topLeft, heb.size, style = Stroke(2.2f * u))
                drawLine(c(LATON_OSC).copy(alpha = queda), Offset(heb.center.x, heb.top), Offset(heb.center.x, heb.bottom), 1.4f * u)
            }
        }
    }

    /**
     * La pestaña del día es una chapa de latón remachada, con el canto de arriba biselado: la letra de la semana grabada
     * arriba, el día grande en el medio y, abajo, el botón de esmalte del ánimo en su color (sin ánimo, el agujero vacío).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u + 2 * u * azar(e.semilla, 2), w - 4 * u, h - 3 * u)
        val forma = Path().apply {
            moveTo(r.left, r.top + 6 * u); lineTo(r.left + 6 * u, r.top); lineTo(r.right - 6 * u, r.top); lineTo(r.right, r.top + 6 * u)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
        }
        translate(1.2f * u, 1.6f * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, c(LATON))
        drawPath(forma, c(LATON_OSC), style = Stroke(1 * u))
        drawLine(c(LATON_LUZ), Offset(r.left + 6 * u, r.top + 1.2f * u), Offset(r.right - 6 * u, r.top + 1.2f * u), 1 * u)
        listOf(Offset(r.left + 4.5f * u, r.top + 9 * u), Offset(r.right - 4.5f * u, r.top + 9 * u)).forEach { drawCircle(c(LATON_OSC), 1.4f * u, it); drawCircle(c(LATON_LUZ), 0.6f * u, it - Offset(0.4f * u, 0.4f * u)) }
        textoEn(medidor, e.semana, Rect(r.left + 8 * u, r.top + 3 * u, r.right - 8 * u, r.top + 13 * u), letra, c(HIERRO_OSC).copy(alpha = 0.75f), 8f, peso = FontWeight.Bold, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left, r.top + 13 * u, r.right, r.top + 33 * u), letra, c(HIERRO_OSC), 18f, peso = FontWeight.Bold, minimo = 9f)
        val boton = Offset(r.center.x, r.bottom - 8.5f * u)
        if (e.tono != null) {
            drawCircle(c(LATON_OSC), 6.5f * u, boton)
            drawCircle(e.tono, 5.2f * u, boton)
            drawCircle(Color.White.copy(alpha = 0.35f), 1.4f * u, boton + Offset(-1.8f * u, -1.8f * u))
        } else {
            drawCircle(c(LATON_OSC), 4 * u, boton, style = Stroke(1 * u))
            drawCircle(c(HIERRO_OSC).copy(alpha = 0.6f), 2.6f * u, boton)
        }
    }

    /** El mes, en una placa de latón atornillada a una viga de hierro remachada que cruza toda la franja. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(HIERRO), Offset(0f, 6 * u), Size(w, h - 12 * u))
        drawLine(c(HIERRO_LUZ), Offset(0f, 6.5f * u), Offset(w, 6.5f * u), 1 * u)
        drawLine(c(HIERRO_OSC), Offset(0f, h - 6.5f * u), Offset(w, h - 6.5f * u), 1 * u)
        var x = 8 * u
        while (x < w) { listOf(10 * u, h - 10 * u).forEach { drawCircle(c(HIERRO_LUZ), 1.4f * u, Offset(x, it)) }; x += 16 * u }
        val medida = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = 13.sp, fontWeight = FontWeight.Bold)).size.width
        val ancho = (medida + 44 * u).coerceIn(w * 0.4f, w - 16 * u)
        val pl = Rect(w / 2 - ancho / 2, 2 * u, w / 2 + ancho / 2, h - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), pl.topLeft + Offset(1.2f * u, 1.6f * u), pl.size, CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(LATON_LUZ), c(LATON), c(LATON_OSC)), pl.top, pl.bottom), pl.topLeft, pl.size, CornerRadius(3 * u))
        drawRoundRect(c(LATON_OSC), pl.deflate(2.5f * u).topLeft, pl.deflate(2.5f * u).size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        listOf(pl.left + 7 * u, pl.right - 7 * u).forEach { sx ->
            drawCircle(c(LATON_OSC), 2.4f * u, Offset(sx, pl.center.y))
            drawLine(c(HIERRO_OSC), Offset(sx - 1.6f * u, pl.center.y - 1.6f * u), Offset(sx + 1.6f * u, pl.center.y + 1.6f * u), 0.8f * u)
        }
        textoEn(medidor, texto, Rect(pl.left + 14 * u, pl.top + 3 * u, pl.right - 14 * u, pl.bottom - 3 * u), letra, c(HIERRO_OSC), 13f, peso = FontWeight.Bold, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El plomo del precinto, el alambre que lo ata y el fieltro de las cápsulas del correo neumático. */
    private val PLOMO = Color(0xFF8C9094)
    private val PLOMO_OSC = Color(0xFF55595D)
    private val ALAMBRE = Color(0xFFB8862B)
    private val FIELTRO = Color(0xFF7A2E22)

    /** Un remache de latón con su brillo. */
    private fun DrawScope.remache(p: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(LATON_OSC, oscuro, false), r, p + Offset(r * 0.25f, r * 0.3f))
        drawCircle(t(LATON, oscuro, false), r, p)
        drawCircle(t(LATON_LUZ, oscuro, false), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** El precinto de plomo: un disco gris aplastado con el engrane estampado y el alambre que lo atraviesa. */
    private fun DrawScope.precintoPlomo(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        drawCircle(Color.Black.copy(alpha = 0.3f * alfa), r, c + Offset(r * 0.1f, r * 0.15f))
        drawCircle(k(PLOMO), r, c)
        drawCircle(k(PLOMO_OSC), r * 0.92f, c, style = Stroke(r * 0.1f))
        drawPath(engrane(c, r * 0.52f, 8), k(PLOMO_OSC), style = Stroke(r * 0.08f))
        drawCircle(k(PLOMO_OSC), r * 0.16f, c)
        drawArc(k(Color.White.copy(alpha = 0.35f)), 200f, 70f, false, Offset(c.x - r * 0.75f, c.y - r * 0.75f), Size(r * 1.5f, r * 1.5f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
    }

    /**
     * El estuche de telegramas de la estación: una caja de hierro pavonado con flejes y cantoneras de latón remachadas y la
     * tapa abisagrada atrás. En el frente, el portatarjetas de latón con la tarjeta crema donde va, grabado, a quién va y las
     * fechas; sobre la tapa, la placa «TELEGRAMA» y el número de la caja. La aldaba baja de la tapa y la cierra el precinto
     * de plomo con el engrane; en las de al futuro, el alambre da vueltas por la armella antes de llegar al plomo. Al abrirla,
     * el plomo se parte, la tapa se levanta sobre sus bisagras y sube el telegrama con su franja de cabecera. En modo oscuro,
     * el hierro se hunde y el latón apenas brilla; la tarjeta sigue clara.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val borde = top + H * 0.12f
        val tapaH = H * 0.3f
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(l + 1.5f * u, borde + 2.5f * u), Size(W, bot - borde), CornerRadius(2 * u))
        // Por dentro: la caja hueca, de hierro oscuro, con su forro de fieltro.
        drawRect(c(HIERRO_OSC), Offset(l, borde - 2 * u), Size(W, H * 0.3f))
        drawRect(c(FIELTRO).copy(alpha = 0.6f), Offset(l + 4 * u, borde), Size(W - 8 * u, H * 0.2f))
        // La tapa levantada, vista por dentro.
        if (k < 0f) {
            val alto = tapaH * -k
            val tapa = Rect(l, top - alto + H * 0.12f, r, top + H * 0.12f)
            drawRect(c(HIERRO), tapa.topLeft, tapa.size)
            drawRect(c(FIELTRO), tapa.deflate(3 * u).topLeft, tapa.deflate(3 * u).size)
            drawRect(c(LATON_OSC), Offset(l, tapa.bottom - 2 * u), Size(W, 2 * u))
        }
        // El telegrama.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.82f, 0.86f, 0.08f, 3 * u)
            papelCartaCo(h, e, 9 * u, 5.5f * u, 6 * u, oscuro)
            drawRect(c(ROJO).copy(alpha = 0.6f), Offset(h.left + 4 * u, h.top + 2.5f * u), Size(h.width - 8 * u, 2.2f * u))
            drawLine(e.renglones, Offset(h.left + 4 * u, h.top + 6.2f * u), Offset(h.right - 4 * u, h.top + 6.2f * u), 0.6f * u)
        }
        // El cuerpo de la caja: hierro pavonado, flejes de latón y cantoneras.
        val caja = Rect(l, borde, r, bot)
        drawRect(c(HIERRO), caja.topLeft, caja.size)
        for (j in 0 until 7) { val y = caja.top + caja.height * azar(e.semilla, j + 50); drawLine(c(HIERRO_LUZ).copy(alpha = 0.18f), Offset(l, y), Offset(r, y), (0.6f + azar(e.semilla, j + 60)) * u) }
        bisel(caja, c(HIERRO_LUZ).copy(alpha = 0.6f), c(HIERRO_OSC), 1.5f * u)
        drawRect(c(LATON), Offset(l, caja.top), Size(W, 3 * u))
        drawRect(c(LATON_OSC), Offset(l, caja.top + 3 * u), Size(W, 0.8f * u))
        drawRect(c(LATON), Offset(l, bot - 4 * u), Size(W, 4 * u))
        listOf(l, r - W * 0.09f).forEach { x ->
            drawRect(c(LATON_OSC), Offset(x, caja.top), Size(W * 0.09f, caja.height))
            drawRect(c(LATON), Offset(x + 0.8f * u, caja.top), Size(W * 0.09f - 1.6f * u, caja.height))
            var y = caja.top + 8 * u
            while (y < bot - 6 * u) { remache(Offset(x + W * 0.045f, y), 1.5f * u, oscuro); y += 9 * u }
        }
        // El portatarjetas con la tarjeta crema.
        val pt = Rect(l + W * 0.11f, top + H * 0.56f, r - W * 0.11f, bot - 7 * u)
        drawRect(c(LATON_OSC), pt.topLeft, pt.size)
        drawRect(c(LATON), pt.deflate(0.8f * u).topLeft, pt.deflate(0.8f * u).size)
        val tj = Rect(pt.left + 3 * u, pt.top + 3 * u, pt.right - 3 * u, pt.bottom - 2 * u)
        drawRect(if (oscuro) mezcla(CARA, Color.Black, 0.18f) else CARA, tj.topLeft, tj.size)
        drawRect(c(LATON_OSC), Offset(pt.left, pt.top), Size(pt.width, 3 * u))
        listOf(pt.left + 1.6f * u, pt.right - 1.6f * u).forEach { x -> remache(Offset(x, pt.top + 1.5f * u), 1.2f * u, oscuro) }
        textoEn(medidor, e.linea1, Rect(tj.left + 3 * u, tj.top + 1 * u, tj.right - 3 * u, tj.top + tj.height * 0.58f), letra, HIERRO_OSC, 11.5f, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(tj.left + 3 * u, tj.top + tj.height * 0.56f, tj.right - 3 * u, tj.bottom - 1 * u), letra, mezcla(HIERRO_OSC, ROJO, 0.35f), 9f, minimo = 6f)
        // La armella del frente, donde baja la aldaba.
        val armella = Offset(cx, top + tapaH + 4 * u)
        drawRoundRect(c(LATON_OSC), Offset(armella.x - 3 * u, armella.y - 3 * u), Size(6 * u, 7 * u), CornerRadius(3 * u), style = Stroke(1.6f * u))
        // La tapa cerrada (o cerrándose), con su placa y la aldaba.
        if (k >= 0f) {
            val alto = tapaH * k
            val tapa = Rect(l - 1 * u, top, r + 1 * u, top + alto)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(tapa.left, tapa.top + 2 * u), tapa.size)
            drawRect(c(HIERRO), tapa.topLeft, tapa.size)
            if (k > 0.15f) scale(1f, k, Offset(cx, top)) {
                val tc = Rect(l - 1 * u, top, r + 1 * u, top + tapaH)
                bisel(tc, c(HIERRO_LUZ).copy(alpha = 0.7f), c(HIERRO_OSC), 1.5f * u)
                drawRect(c(LATON), Offset(tc.left, tc.bottom - 3 * u), Size(tc.width, 3 * u))
                var x = tc.left + 6 * u
                while (x < tc.right - 4 * u) { remache(Offset(x, tc.bottom - 1.5f * u), 1.1f * u, oscuro); x += 10 * u }
                val pl = Rect(l + W * 0.14f, top + tapaH * 0.18f, l + W * 0.56f, top + tapaH * 0.7f)
                drawRoundRect(c(LATON_OSC), pl.topLeft, pl.size, CornerRadius(2 * u))
                drawRoundRect(c(LATON), pl.deflate(1 * u).topLeft, pl.deflate(1 * u).size, CornerRadius(1.5f * u))
                textoEn(medidor, "TELEGRAMA", pl.deflate(2.5f * u), letra, c(HIERRO_OSC), 8.5f, peso = FontWeight.Bold, espaciado = 1f, minimo = 4f)
                val ng = Offset(r - W * 0.2f, top + tapaH * 0.44f)
                drawPath(engrane(ng, tapaH * 0.3f, 10), c(LATON_OSC))
                drawCircle(c(LATON), tapaH * 0.18f, ng)
                textoEn(medidor, "${(e.semilla and 0x7fffffff) % 90 + 10}", Rect(ng.x - tapaH * 0.16f, ng.y - tapaH * 0.14f, ng.x + tapaH * 0.16f, ng.y + tapaH * 0.14f), letra, c(HIERRO_OSC), 7f, peso = FontWeight.Bold, minimo = 3f)
                // La aldaba: una lengüeta de latón que baja hasta la armella.
                drawRoundRect(c(LATON_OSC), Offset(cx - 4 * u, top + tapaH * 0.55f), Size(8 * u, tapaH * 0.45f + 6 * u), CornerRadius(2 * u))
                drawRoundRect(c(LATON), Offset(cx - 3.2f * u, top + tapaH * 0.55f), Size(6.4f * u, tapaH * 0.45f + 5 * u), CornerRadius(2 * u))
                remache(Offset(cx, top + tapaH * 0.62f), 1.2f * u, oscuro)
            }
        }
        // El alambre del precinto: da vueltas por la armella y baja al plomo.
        val selloR = H * 0.11f
        val plomo = Offset(cx + selloR * 1.6f, armella.y + selloR * 0.9f)
        val selloC = if (k >= 0f) plomo else plomo
        if (e.cordon > 0f) {
            val p = Path().apply {
                moveTo(cx - 7 * u, armella.y - 1 * u)
                for (j in 0 until 4) { cubicTo(cx - 3 * u, armella.y - 6 * u, cx + 3 * u, armella.y + 5 * u, cx + 5 * u, armella.y); cubicTo(cx + 3 * u, armella.y - 5 * u, cx - 3 * u, armella.y + 5 * u, cx - 5 * u, armella.y) }
                quadraticTo(cx + selloR * 0.4f, armella.y + selloR * 1.2f, plomo.x, plomo.y)
            }
            trazoParcialCo(p, e.cordon, c(ALAMBRE), 1 * u)
        } else if (e.alfaSello > 0f && e.romper < 1f) {
            drawLine(c(ALAMBRE).copy(alpha = e.alfaSello), Offset(cx, armella.y), plomo, 0.9f * u)
        }
        if (e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(GAS), e.brillo)
            selloPartidoCo(plomo, selloR, e.romper, e.escalaSello, Offset(-selloR * 0.5f * e.romper, -selloR * 0.4f * e.romper - (if (k < 0f) H * 0.5f else 0f)),
                Offset(selloR * 0.3f * e.romper, selloR * 0.5f * e.romper), e.semilla, conArriba = k >= 0f) { precintoPlomo(plomo, selloR, oscuro, e.alfaSello) }
        }
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.25f, 0.84f, 0.395f)

    override val altoBuzon: Dp = 204.dp

    /** Una cápsula del correo neumático, tumbada: latón con dos fajas de cuero y tapas de fieltro; lista, sellada o abierta. */
    private fun DrawScope.capsula(r: Rect, tipo: Int, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val rr = r.height / 2
        if (tipo == 2) {
            // Abierta: sin la tapa de la izquierda, con el telegrama enrollado asomando.
            drawRoundRect(c(CARA), Offset(r.left - rr * 1.3f, r.top + rr * 0.25f), Size(rr * 2.4f, rr * 1.5f), CornerRadius(rr * 0.75f))
            drawArc(c(TABLON_VETA).copy(alpha = 0.6f), 90f, 180f, false, Offset(r.left - rr * 1.3f, r.top + rr * 0.25f), Size(rr * 1.5f, rr * 1.5f), style = Stroke(0.8f * u))
        }
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1 * u, 1.5f * u), r.size, CornerRadius(rr))
        drawRoundRect(c(LATON), r.topLeft, r.size, CornerRadius(rr))
        drawRoundRect(c(LATON_OSC), Offset(r.left + rr, r.bottom - rr * 0.5f), Size(r.width - 2 * rr, rr * 0.5f), CornerRadius(rr * 0.25f))
        drawLine(c(LATON_LUZ), Offset(r.left + rr, r.top + rr * 0.45f), Offset(r.right - rr, r.top + rr * 0.45f), rr * 0.25f, StrokeCap.Round)
        listOf(0.3f, 0.7f).forEach { f -> drawRect(c(CORREA), Offset(r.left + r.width * f - rr * 0.35f, r.top), Size(rr * 0.7f, r.height)) }
        drawRoundRect(c(FIELTRO), Offset(r.right - rr * 0.9f, r.top), Size(rr * 0.9f, r.height), CornerRadius(rr * 0.45f))
        if (tipo != 2) drawRoundRect(c(FIELTRO), Offset(r.left, r.top), Size(rr * 0.9f, r.height), CornerRadius(rr * 0.45f))
        else drawOval(c(HIERRO_OSC), Offset(r.left - rr * 0.2f, r.top + rr * 0.1f), Size(rr * 0.6f, r.height - rr * 0.2f))
        if (tipo == 1) {
            val p = Offset(r.left + r.width * 0.7f, r.bottom + rr * 0.55f)
            drawLine(c(ALAMBRE), Offset(r.left + r.width * 0.7f, r.center.y), p, 0.8f * u)
            precintoPlomo(p, rr * 0.55f, oscuro, 1f)
        }
        if (tipo == 0) {
            // La banderita roja de «llegada».
            val m = Offset(r.left + r.width * 0.5f, r.top)
            drawLine(c(HIERRO_OSC), m, m - Offset(0f, rr * 1.4f), 0.9f * u)
            drawPath(Path().apply { moveTo(m.x, m.y - rr * 1.4f); lineTo(m.x + rr * 1.1f, m.y - rr * 1.1f); lineTo(m.x, m.y - rr * 0.8f); close() }, c(ROJO))
        }
    }

    /** Un contador de rodillos: la caja de latón con su rótulo y los tres tambores blancos con la cifra. */
    private fun DrawScope.contadorRodillos(r: Rect, rotulo: String, n: Int, medidor: TextMeasurer, oscuro: Boolean, vivo: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1 * u, 1.5f * u), r.size, CornerRadius(3 * u))
        drawRoundRect(c(LATON_OSC), r.topLeft, r.size, CornerRadius(3 * u))
        drawRoundRect(c(LATON), r.deflate(1.2f * u).topLeft, r.deflate(1.2f * u).size, CornerRadius(2.5f * u))
        textoEn(medidor, rotulo, Rect(r.left + 4 * u, r.top + 2 * u, r.right - 4 * u, r.top + r.height * 0.36f), letra, c(HIERRO_OSC), 8f, peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
        val cifras = "%03d".format(n.coerceIn(0, 999))
        val ventana = Rect(r.center.x - r.height * 0.75f, r.top + r.height * 0.4f, r.center.x + r.height * 0.75f, r.bottom - 4 * u)
        drawRect(c(HIERRO_OSC), ventana.inflate(1.5f * u).topLeft, ventana.inflate(1.5f * u).size)
        val dw = ventana.width / 3
        cifras.forEachIndexed { i, ch ->
            val d = Rect(ventana.left + i * dw + 0.6f * u, ventana.top, ventana.left + (i + 1) * dw - 0.6f * u, ventana.bottom)
            drawRect(if (oscuro) mezcla(CARA, Color.Black, 0.15f) else CARA, d.topLeft, d.size)
            drawRect(Color.Black.copy(alpha = 0.2f), d.topLeft, Size(d.width, d.height * 0.18f))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(d.left, d.bottom - d.height * 0.18f), Size(d.width, d.height * 0.18f))
            textoEn(medidor, ch.toString(), d, letra, if (vivo) ROJO else HIERRO_OSC, 13f, peso = FontWeight.Bold, minimo = 6f)
        }
        listOf(r.left + 4 * u, r.right - 4 * u).forEach { x -> remache(Offset(x, r.center.y + r.height * 0.15f), 1.3f * u, oscuro) }
    }

    /**
     * La estación del correo neumático, contra el muro de ladrillo tiznado: el tubo de latón baja del techo con su manómetro
     * y su brida hasta el recibidor de hierro con la compuerta; a la derecha, el portacápsulas de hierro colado, de dos
     * repisas, con una cápsula por carta (hasta diez): las llegadas con su banderita roja y el resplandor de la lámpara de
     * gas (el primer degradado), las precintadas con su plomo colgando y las abiertas sin tapa, con el telegrama asomando.
     * Abajo, tres contadores de rodillos en su caja de latón con la cuenta de cada clase. En modo oscuro, la estación de
     * noche bajo la lámpara de gas del muro (el segundo degradado).
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0B0907), 0.35f) else t(x, false, false)
        // El muro de ladrillo.
        drawRect(m(LADRILLO))
        val lh = 9 * u; val lw = 24 * u
        var fila = 0; var y = 0f
        while (y < h) {
            drawLine(m(LADRILLO_JUNTA), Offset(0f, y), Offset(w, y), 1.2f * u)
            var x = if (fila % 2 == 0) 0f else -lw / 2
            while (x < w) { drawLine(m(LADRILLO_JUNTA), Offset(x, y), Offset(x, y + lh), 1.2f * u); drawRect(Color.Black.copy(alpha = 0.12f * azar(fila, (x / u).toInt())), Offset(x + 1 * u, y + 1 * u), Size(lw - 2 * u, lh - 2 * u)); x += lw }
            y += lh; fila++
        }
        if (oscuro) mancha(Offset(w * 0.12f, h * 0.18f), w * 0.5f, c(GAS).copy(alpha = 0.22f))
        // El tubo de latón y el recibidor.
        val tx = w * 0.075f; val tg = 16 * u
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tx - tg / 2 + 3 * u, 0f), Size(tg, h * 0.42f))
        drawRect(c(LATON_OSC), Offset(tx - tg / 2, 0f), Size(tg, h * 0.42f))
        drawRect(c(LATON), Offset(tx - tg / 2 + 1.5f * u, 0f), Size(tg - 5 * u, h * 0.42f))
        drawRect(c(LATON_LUZ), Offset(tx - tg / 2 + 3.5f * u, 0f), Size(2.5f * u, h * 0.42f))
        listOf(h * 0.08f, h * 0.36f).forEach { by ->
            drawRect(c(LATON_OSC), Offset(tx - tg / 2 - 3 * u, by), Size(tg + 6 * u, 5 * u))
            listOf(tx - tg / 2 - 1 * u, tx + tg / 2 + 1 * u).forEach { bx -> drawCircle(c(HIERRO), 1.2f * u, Offset(bx, by + 2.5f * u)) }
        }
        manometro(Offset(tx + tg / 2 + 12 * u, h * 0.2f), 11 * u, c(LATON), c(CARA), c(ROJO), 0.3f + 0.6f * ((e.listas + e.selladas + e.abiertas).coerceAtMost(30) / 30f))
        drawLine(c(LATON_OSC), Offset(tx + tg / 2, h * 0.2f), Offset(tx + tg / 2 + 2 * u, h * 0.2f), 3 * u)
        val rec = Rect(tx - 15 * u, h * 0.42f, tx + 15 * u, h * 0.66f)
        drawRect(Color.Black.copy(alpha = 0.3f), rec.topLeft + Offset(2 * u, 2 * u), rec.size)
        drawRoundRect(c(HIERRO), rec.topLeft, rec.size, CornerRadius(4 * u))
        bisel(rec, c(HIERRO_LUZ).copy(alpha = 0.5f), c(HIERRO_OSC), 1.2f * u)
        drawCircle(c(HIERRO_OSC), 9 * u, rec.center)
        drawCircle(c(LATON), 9 * u, rec.center, style = Stroke(2 * u))
        drawLine(c(LATON), rec.center - Offset(6 * u, 0f), rec.center + Offset(6 * u, 0f), 2 * u, StrokeCap.Round)
        drawRect(c(HIERRO_OSC), Offset(rec.left + 4 * u, rec.bottom), Size(rec.width - 8 * u, h * 0.06f))
        // El portacápsulas.
        val rack = Rect(w * 0.2f, 8 * u, w - 8 * u, h * 0.64f)
        val cartas = cartasBuzonCo(e, 10)
        for (f in 0 until 2) {
            val ry = rack.top + (rack.height / 2) * (f + 1) - 4 * u
            val cw = rack.width / 5
            for (k in 0 until 5) {
                val tipo = cartas.getOrNull(f * 5 + k) ?: continue
                val cr = Rect(rack.left + k * cw + cw * 0.18f, ry - 15 * u, rack.left + (k + 1) * cw - cw * 0.08f, ry - 2 * u)
                if (tipo == 0) mancha(cr.center, cw * 0.62f, c(GAS).copy(alpha = 0.5f))
                capsula(cr, tipo, oscuro)
            }
            // La repisa: una barra de hierro colado con sus ménsulas caladas.
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(rack.left, ry + 2 * u), Size(rack.width, 4 * u))
            drawRect(c(HIERRO), Offset(rack.left, ry), Size(rack.width, 4 * u))
            drawRect(c(HIERRO_LUZ).copy(alpha = 0.6f), Offset(rack.left, ry), Size(rack.width, 1 * u))
            for (k in 0..5) {
                val mx = rack.left + k * rack.width / 5
                drawPath(Path().apply { moveTo(mx - 1.5f * u, ry + 4 * u); lineTo(mx + 1.5f * u, ry + 4 * u); lineTo(mx + 1.5f * u, ry + 13 * u); lineTo(mx - 1.5f * u, ry + 13 * u); close() }, c(HIERRO))
                drawCircle(c(HIERRO), 3.5f * u, Offset(mx, ry + 8 * u), style = Stroke(1.2f * u))
            }
        }
        // Los tres contadores de rodillos.
        val cuentas = listOf("LLEGADAS" to e.listas, "PRECINTADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val base = Rect(w * 0.2f, h * 0.7f, w - 8 * u, h - 6 * u)
        val pw = base.width / 3
        cuentas.forEachIndexed { i, (txt, n) ->
            contadorRodillos(Rect(base.left + i * pw + 3 * u, base.top, base.left + (i + 1) * pw - 3 * u, base.bottom), txt, n, medidor, oscuro, i == 0 && n > 0)
        }
        drawRect(c(HIERRO), Offset(0f, h - 4 * u), Size(w, 4 * u))
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
 * La tapa es ese librito de tafilete de frente, con «BULLETIN DE LA GRANDE ARMÉE» dorado, la N en su laurel, las abejas,
 * la cartela de pergamino y la fecha en el tejuelo; al abrirlo, se ve la hoja impresa del boletín.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.36f, 0.29f, 0.88f, 0.72f)
    override val letraTapa: FontFamily = LetraExtra.bodoni
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El pergamino de la cartela de la tapa: hundido en oscuro y entibiado de noche, pero claro para el extracto. */
    private fun pergaminoTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PERGAMINO
        if (oscuro) r = mezcla(r, Color(0xFF0E0B08), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.14f)
        return r
    }

    /** Una abeja dorada del Imperio: las dos alas, el cuerpo y la cabeza. */
    private fun DrawScope.abeja(o: Offset, s: Float, oro: Color) {
        drawOval(oro.copy(alpha = 0.6f), Offset(o.x - s * 0.95f, o.y - s * 0.75f), Size(s * 0.85f, s * 0.55f))
        drawOval(oro.copy(alpha = 0.6f), Offset(o.x + s * 0.1f, o.y - s * 0.75f), Size(s * 0.85f, s * 0.55f))
        drawOval(oro, Offset(o.x - s * 0.26f, o.y - s * 0.45f), Size(s * 0.52f, s * 1.15f))
        drawCircle(oro, s * 0.2f, Offset(o.x, o.y - s * 0.55f))
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val tafilete = c(TAFILETES[(e.semilla and 0x7fffffff) % TAFILETES.size])
        val oro = c(ORO)
        val cu = Rect(w * 0.02f, h * 0.05f, w * 0.97f, h * 0.94f)
        val bisagra = cu.left + w * 0.07f
        // De noche, el farol de campaña alumbra desde lo alto del mástil (el único degradado).
        if (e.noche) mancha(Offset(w * 0.96f, 0f), w * 0.5f, FAROL.copy(alpha = 0.26f))
        drawRoundRect(Color.Black.copy(alpha = 0.22f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(2.5f * u))
        // El canto de las hojas, dorado.
        drawRect(c(PERGAMINO), Offset(cu.left + 3 * u, cu.top + 3 * u), cu.size)
        drawRect(oro.copy(alpha = 0.55f), Offset(cu.right, cu.top + 4 * u), Size(3 * u, cu.height - 1 * u))
        drawRect(oro.copy(alpha = 0.55f), Offset(cu.left + 6 * u, cu.bottom), Size(cu.width - 3 * u, 3 * u))
        // Por dentro (se ve al abrirse): la hoja impresa del boletín, con su cabecera entre filetes y dos columnas.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(PERGAMINO), hoja.topLeft, hoja.size)
            val x0 = hoja.left + 12 * u; val x1 = hoja.right - 10 * u
            listOf(0.1f to 2.2f, 0.125f to 0.7f, 0.26f to 0.7f, 0.285f to 2.2f).forEach { (f, g) -> drawLine(c(TINTA), Offset(x0, h * f), Offset(x1, h * f), g * u) }
            drawRect(c(TINTA).copy(alpha = 0.55f), Offset(x0 + (x1 - x0) * 0.15f, h * 0.16f), Size((x1 - x0) * 0.7f, h * 0.06f))
            val medio = (x0 + x1) / 2
            drawLine(c(TINTA).copy(alpha = 0.4f), Offset(medio, h * 0.32f), Offset(medio, h * 0.88f), 0.6f * u)
            var ly = h * 0.33f; var k = 0
            while (ly < h * 0.88f) {
                drawLine(c(TINTA).copy(alpha = 0.22f), Offset(x0, ly), Offset(medio - 5 * u - (if (k % 6 == 5) (medio - x0) * 0.4f else 0f), ly), 1.4f * u)
                drawLine(c(TINTA).copy(alpha = 0.22f), Offset(medio + 5 * u, ly), Offset(x1 - (if (k % 7 == 6) (x1 - medio) * 0.5f else 0f), ly), 1.4f * u)
                ly += 4.5f * u; k++
            }
        }
        // La tapa de tafilete, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(tafilete, cu.topLeft, cu.size, CornerRadius(2.5f * u))
            for (j in 0 until 160) drawCircle(Color.Black.copy(alpha = 0.1f), 0.55f * u, Offset(cu.left + cu.width * azar(e.semilla, 60 + j), cu.top + cu.height * azar(e.semilla, 260 + j)))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cu.right - 4 * u, cu.top), Size(4 * u, cu.height))
            drawRect(Color.White.copy(alpha = 0.06f), Offset(bisagra, cu.top), Size(cu.width - (bisagra - cu.left), 2.5f * u))
            // El doble filete dorado, con un florón en cada esquina.
            val marco = Rect(bisagra + 7 * u, cu.top + 7 * u, cu.right - 7 * u, cu.bottom - 7 * u)
            drawRect(oro, marco.topLeft, marco.size, style = Stroke(1.1f * u))
            drawRect(oro.copy(alpha = 0.75f), marco.deflate(3 * u).topLeft, marco.deflate(3 * u).size, style = Stroke(0.6f * u))
            listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { o ->
                drawPath(estrella(o, 3.6f * u, 4, 0.35f, giro = 0f), oro)
                drawCircle(tafilete, 0.9f * u, o)
            }
            // El título del boletín, dorado, y su filete con dos estrellas.
            textoEn(medidor, "BULLETIN DE LA GRANDE ARMÉE", Rect(bisagra + 16 * u, h * 0.1f, cu.right - 16 * u, h * 0.195f), letra, oro, 12f,
                peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 7f)
            drawLine(oro, Offset(bisagra + 26 * u, h * 0.215f), Offset(cu.right - 26 * u, h * 0.215f), 0.7f * u)
            listOf(bisagra + 20 * u, cu.right - 20 * u).forEach { x -> drawPath(estrella(Offset(x, h * 0.215f), 3 * u, 5, 0.42f), oro) }
            // La N en su corona de laurel, con la corona imperial encima y el lazo abajo.
            val cn = Offset((bisagra + w * 0.33f) / 2 + 2 * u, h * 0.53f)
            val rn = minOf(w * 0.095f, h * 0.15f)
            drawArc(oro, 100f, 160f, false, Offset(cn.x - rn, cn.y - rn), Size(rn * 2, rn * 2), style = Stroke(0.9f * u))
            drawArc(oro, -80f, 160f, false, Offset(cn.x - rn, cn.y - rn), Size(rn * 2, rn * 2), style = Stroke(0.9f * u))
            for (j in 0 until 7) {
                val g = 112.0 + j * 21.0
                listOf(g, 180.0 - g).forEach { gr ->
                    val ang = Math.toRadians(gr).toFloat()
                    val p = Offset(cn.x + rn * cos(ang), cn.y + rn * sin(ang))
                    rotate(gr.toFloat() + 90f + (if (gr > 90.0) 30f else -30f), p) { drawOval(oro, Offset(p.x - rn * 0.17f, p.y - rn * 0.07f), Size(rn * 0.34f, rn * 0.14f)) }
                }
            }
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(cn.x, cn.y + rn); lineTo(cn.x + s * rn * 0.38f, cn.y + rn * 1.28f); lineTo(cn.x + s * rn * 0.26f, cn.y + rn * 1.36f); close()
                }, c(TEJUELO))
            }
            textoEn(medidor, "N", Rect(cn, rn * 0.72f), letra, oro, 28f, peso = FontWeight.Bold, minimo = 8f)
            val cc = Offset(cn.x, cn.y - rn - 6 * u)
            drawArc(oro, 180f, 180f, true, Offset(cc.x - 7 * u, cc.y - 4 * u), Size(14 * u, 8 * u))
            drawRect(oro, Offset(cc.x - 7.5f * u, cc.y), Size(15 * u, 2 * u))
            listOf(-5f, 0f, 5f).forEach { dx -> drawCircle(oro, 1.1f * u, Offset(cc.x + dx * u, cc.y - 4.5f * u - (if (dx == 0f) 1.4f * u else 0f))) }
            // Las abejas sembradas alrededor: más cuanto más escribiste; de noche, la primera es una luna.
            val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
            val abejas = 1 + (lleno * 4).toInt()
            val sitios = listOf(Offset(cn.x - rn * 0.9f, h * 0.3f), Offset(cn.x + rn * 0.9f, h * 0.3f), Offset(cn.x - rn * 0.9f, h * 0.745f),
                Offset(cn.x + rn * 0.9f, h * 0.745f), Offset(cn.x, h * 0.775f))
            val sb = 5.2f * u
            for (j in 0 until abejas) {
                val o = sitios[j]
                if (j == 0 && e.noche) {
                    drawCircle(LUNA, sb * 0.95f, o)
                    drawCircle(tafilete, sb * 0.8f, Offset(o.x + sb * 0.42f, o.y - sb * 0.24f))
                } else abeja(o, sb, oro)
            }
            // La cartela de pergamino, de esquinas cortadas, donde va el extracto.
            val ct = Rect(w * 0.33f, h * 0.255f, w * 0.91f, h * 0.755f)
            val kc = 8 * u
            fun octogono(r: Rect, k: Float) = Path().apply {
                moveTo(r.left + k, r.top); lineTo(r.right - k, r.top); lineTo(r.right, r.top + k); lineTo(r.right, r.bottom - k)
                lineTo(r.right - k, r.bottom); lineTo(r.left + k, r.bottom); lineTo(r.left, r.bottom - k); lineTo(r.left, r.top + k); close()
            }
            drawPath(octogono(Rect(ct.left + 1.2f * u, ct.top + 1.8f * u, ct.right + 1.2f * u, ct.bottom + 1.8f * u), kc), Color.Black.copy(alpha = 0.3f))
            drawPath(octogono(ct, kc), pergaminoTapa(oscuro, e.noche))
            drawPath(octogono(ct, kc), oro, style = Stroke(1.2f * u))
            drawPath(octogono(ct.deflate(3 * u), kc * 0.7f), oro.copy(alpha = 0.6f), style = Stroke(0.6f * u))
            // El tejuelo rojo con la fecha, dorada.
            val tj = Rect(w * 0.38f, h * 0.785f, w * 0.86f, h * 0.88f)
            drawRect(c(TEJUELO), tj.topLeft, tj.size)
            drawRect(oro, tj.deflate(1.6f * u).topLeft, tj.deflate(1.6f * u).size, style = Stroke(0.6f * u))
            textoEn(medidor, e.fecha, tj.deflate(3.5f * u), letra, oro, 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(bisagra + 10 * u, h * 0.81f, w * 0.355f, h * 0.875f), LetraExtra.bodoni, oro, 9f, minimo = 6f)
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(2.5f * u))
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo, con sus filetes dorados en pares.
        drawRect(tafilete, cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.Black.copy(alpha = 0.3f), cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.White.copy(alpha = 0.07f), Offset(cu.left + (bisagra - cu.left) * 0.3f, cu.top), Size((bisagra - cu.left) * 0.2f, cu.height))
        listOf(0.08f, 0.3f, 0.5f, 0.7f, 0.92f).forEach { f ->
            val y = cu.top + cu.height * f
            drawLine(oro, Offset(cu.left + 2 * u, y), Offset(bisagra - 2 * u, y), 0.6f * u)
            drawLine(oro, Offset(cu.left + 2 * u, y + 1.8f * u), Offset(bisagra - 2 * u, y + 1.8f * u), 0.5f * u)
        }
        listOf(0.19f, 0.6f, 0.81f).forEach { f -> drawPath(estrella(Offset((cu.left + bisagra) / 2, cu.top + cu.height * f + 0.9f * u), 2.4f * u, 5, 0.42f), oro) }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 1 * u)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 128.dp

    /** Una corona de laurel abierta arriba: dos ramas de hojas en arco, en dos hileras, que se juntan abajo con un lazo. */
    private fun DrawScope.laurel(c0: Offset, r: Float, color: Color) {
        val u = density
        drawArc(color, 114f, 133f, false, Offset(c0.x - r, c0.y - r), Size(2 * r, 2 * r), style = Stroke(maxOf(0.8f * u, r * 0.05f)))
        drawArc(color, -67f, 133f, false, Offset(c0.x - r, c0.y - r), Size(2 * r, 2 * r), style = Stroke(maxOf(0.8f * u, r * 0.05f)))
        listOf(-1f, 1f).forEach { lado ->
            for (k in 0 until 8) {
                val grados = 90.0 + lado * (24.0 + k * 19.0)
                val a = Math.toRadians(grados)
                val rr = r * (if (k % 2 == 0) 1.1f else 0.9f)
                val p0 = Offset(c0.x + rr * cos(a).toFloat(), c0.y + rr * sin(a).toFloat())
                rotate(grados.toFloat() + lado * 18f, p0) {
                    drawOval(color, Offset(p0.x - r * 0.1f, p0.y - r * 0.24f), Size(r * 0.2f, r * 0.48f))
                }
            }
        }
        drawCircle(color, r * 0.09f, Offset(c0.x - r * 0.12f, c0.y + r * 0.98f))
        drawCircle(color, r * 0.09f, Offset(c0.x + r * 0.12f, c0.y + r * 0.98f))
    }

    /**
     * La cabecera del boletín: arriba, la N en su corona de laurel entre dos filetes dobles con una abeja en cada uno; el
     * nombre del boletín en versales espaciadas, el filete tricolor, la fecha en Bodoni y el detalle; al pie, el filete fino
     * y el grueso. De noche, la luna en la corona y estrellas en lugar de las abejas.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val cx = w / 2
        val laurelColor = if (e.noche) p.acento else mezcla(c(ORO), p.tinta, 0.35f)
        val corona = Offset(cx, 18 * u)
        laurel(corona, 13 * u, laurelColor)
        if (e.noche) {
            crecienteCon(corona, 7 * u, p.acento)
        } else {
            textoEn(medidor, "N", Rect(cx - 9 * u, corona.y - 9 * u, cx + 9 * u, corona.y + 9 * u), LetraExtra.bodoniTitulo, p.acento, 15f, peso = FontWeight.Bold, minimo = 9f)
        }
        // Los filetes dobles a los lados de la corona, con su abeja (de noche, una estrella).
        listOf(8 * u to cx - 24 * u, cx + 24 * u to w - 8 * u).forEachIndexed { i, (a, b) ->
            val medio = (a + b) / 2
            listOf(a to medio - 9 * u, medio + 9 * u to b).forEach { (x0, x1) ->
                drawLine(p.tinta.copy(alpha = 0.7f), Offset(x0, 16.5f * u), Offset(x1, 16.5f * u), 1.3f * u)
                drawLine(p.tinta.copy(alpha = 0.6f), Offset(x0, 20 * u), Offset(x1, 20 * u), 0.6f * u)
            }
            if (e.noche) drawPath(estrella(Offset(medio, 18 * u), 4.5f * u, 5, 0.42f), p.acento)
            else abeja(Offset(medio, 19.5f * u), 7 * u, mezcla(c(ORO), p.tinta, 0.3f))
        }
        textoEn(medidor, e.rotulo, Rect(6 * u, 36 * u, w - 6 * u, 51 * u), LetraExtra.bodoniTitulo, p.acento, 11f, espaciado = 1.2f, minimo = 6.5f)
        // El filete tricolor.
        val tw = w * 0.1f
        drawRect(c(Color(0xFF1E3A8A)), Offset(cx - tw * 1.5f, 54.5f * u), Size(tw, 2 * u))
        drawRect(p.tenue.copy(alpha = 0.5f), Offset(cx - tw * 0.5f, 54.5f * u), Size(tw, 2 * u), style = Stroke(0.5f * u))
        drawRect(c(TEJUELO), Offset(cx + tw * 0.5f, 54.5f * u), Size(tw, 2 * u))
        fechaCon(medidor, e.fecha, Rect(6 * u, 60 * u, w - 6 * u, 101 * u), LetraExtra.bodoni, p.tinta, 23f)
        textoEn(medidor, e.detalle, Rect(6 * u, 102 * u, w - 6 * u, 116 * u), LetraExtra.bodoni, p.tenue, 11f, minimo = 6.5f)
        drawRect(p.tinta.copy(alpha = 0.8f), Offset(0f, 119.5f * u), Size(w, 0.7f * u))
        drawRect(p.tinta.copy(alpha = 0.8f), Offset(0f, 122 * u), Size(w, 2.2f * u))
    }

    /**
     * El papel vergé del boletín: los corondeles y los puntizones, la marca de agua de la corona de laurel al centro, el
     * doble marco azul y rojo con una abeja en cada esquina y la escarapela tricolor al pie. En modo oscuro, el farol de
     * campaña puesto en la mesa, abajo a la izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        var x = 11 * u
        while (x < w) { drawLine(p.tenue.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 0.8f * u); x += 24 * u }
        var y = 0f
        while (y < h) { drawLine(p.tenue.copy(alpha = 0.022f), Offset(0f, y), Offset(w, y), 0.5f * u); y += 2.6f * u }
        // La marca de agua: la corona de laurel, grande y casi invisible.
        laurel(Offset(w / 2, h * 0.5f), w * 0.24f, p.tinta.copy(alpha = 0.035f))
        drawRect(p.acento.copy(alpha = 0.38f), Offset(9 * u, 9 * u), Size(w - 18 * u, h - 18 * u), style = Stroke(1.3f * u))
        drawRect(p.detalle.copy(alpha = 0.3f), Offset(13 * u, 13 * u), Size(w - 26 * u, h - 26 * u), style = Stroke(0.7f * u))
        listOf(Offset(22 * u, 22 * u), Offset(w - 22 * u, 22 * u), Offset(22 * u, h - 22 * u), Offset(w - 22 * u, h - 22 * u)).forEach { o ->
            abeja(o, 5 * u, mezcla(c(ORO), p.tinta, 0.3f).copy(alpha = 0.55f))
        }
        // La escarapela tricolor al pie.
        val es = Offset(w / 2, h - 30 * u)
        drawCircle(c(Color(0xFF1E3A8A)).copy(alpha = 0.45f), 7.5f * u, es)
        drawCircle(mezcla(p.color, Color.White, 0.6f), 5 * u, es)
        drawCircle(c(TEJUELO).copy(alpha = 0.55f), 2.7f * u, es)
        drawLine(p.acento.copy(alpha = 0.3f), Offset(w * 0.3f, es.y), Offset(es.x - 11 * u, es.y), 0.7f * u)
        drawLine(p.acento.copy(alpha = 0.3f), Offset(es.x + 11 * u, es.y), Offset(w * 0.7f, es.y), 0.7f * u)
        if (oscuro && !p.oscura) {
            val farol = Offset(w * 0.08f, h * 0.95f)
            penumbraCon(farol, FAROL, 0.2f)
            hazCon(farol, -55f, 26f, h * 0.75f, FAROL.copy(alpha = 0.03f))
        }
    }

    /** La cinta tricolor al pie de la barra, entre dos filetes de oro. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        val y = h - 6 * u
        drawLine(c(ORO).copy(alpha = 0.85f), Offset(0f, y - 1.3f * u), Offset(w, y - 1.3f * u), 0.8f * u)
        drawRect(c(Color(0xFF1E3A8A)), Offset(0f, y), Size(w / 3, 3.5f * u))
        drawRect(c(Color(0xFFF2EEE4)), Offset(w / 3, y), Size(w / 3, 3.5f * u))
        drawRect(c(TEJUELO), Offset(w * 2 / 3, y), Size(w / 3 + 1, 3.5f * u))
        drawLine(c(ORO).copy(alpha = 0.85f), Offset(0f, y + 4.8f * u), Offset(w, y + 4.8f * u), 0.8f * u)
    }

    /** El tejuelo rojo de tafilete con su doble filete dorado y una abeja en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1 * u, 1.8f * u), size, CornerRadius(2 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(mezcla(TEJUELO, Color.White, 0.12f)), c(TEJUELO), c(mezcla(TEJUELO, Color.Black, 0.25f)))), Offset.Zero, size, CornerRadius(2 * u))
        drawRect(c(ORO), Offset(2.5f * u, 2.5f * u), Size(w - 5 * u, h - 5 * u), style = Stroke(0.9f * u))
        drawRect(c(ORO).copy(alpha = 0.7f), Offset(4.5f * u, 4.5f * u), Size(w - 9 * u, h - 9 * u), style = Stroke(0.5f * u))
        abeja(Offset(9 * u, h / 2 + 1 * u), 3 * u, c(ORO))
        abeja(Offset(w - 9 * u, h / 2 + 1 * u), 3 * u, c(ORO))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF6E7B8)

    // ---------------------------------------------------------------- la libreta de momentos

    /** El paño verde del tablero de campaña, el cordón rojo del legajo, el lacre y el papel de los despachos. */
    private val TAPETE = Color(0xFF2F5A3E)
    private val CORDON = Color(0xFFB5262B)
    private val LACRE = Color(0xFF8E1B1F)
    private val DESPACHO = Color(0xFFF1E8D0)

    override val altoLibreta: Dp = 254.dp

    /** Una ramita de laurel: el tallo curvo desde [c] hacia [s] (1 a la derecha, -1 a la izquierda) y sus hojas alternas. */
    private fun DrawScope.ramaLaurel(c: Offset, largo: Float, s: Float, color: Color) {
        val u = density
        drawPath(Path().apply { moveTo(c.x, c.y); quadraticTo(c.x + s * largo * 0.6f, c.y + largo * 0.1f, c.x + s * largo, c.y - largo * 0.45f) }, color, style = Stroke(0.9f * u))
        for (k in 1..5) {
            val f = k / 6f
            val p = Offset(c.x + s * largo * f, c.y + largo * 0.1f * f - largo * 0.55f * f * f)
            listOf(-1f, 1f).forEach { lado ->
                val q = Offset(p.x + s * largo * 0.06f, p.y + lado * largo * 0.12f)
                val m = Offset((p.x + q.x) / 2, (p.y + q.y) / 2)
                drawOval(color, Offset(m.x - largo * 0.07f, m.y - largo * 0.035f), Size(largo * 0.14f, largo * 0.07f))
            }
        }
    }

    /**
     * El legajo de despachos sobre la mesa de campaña plegable, delante de la lona de la tienda a rayas azules: el tablero de
     * caoba con su paño verde y sus patas en tijera, el mapa abierto debajo, el catalejo de latón con su funda de cuero a la
     * izquierda y, a la derecha, el tintero con la pluma de ganso y el farol de campaña. El legajo es una carpeta de tafilete
     * azul con su filete dorado, la cartela de pergamino con el nombre y la cuenta, y el cordón rojo en cruz anudado bajo el
     * sello de lacre del laurel; engorda con las hojas y por arriba asoman los despachos doblados con su lacre (uno cada 10
     * hojas, hasta 9). Al abrirse, el cordón se suelta y la tapa gira sobre el lomo: dentro, el despacho de encima con su sello.
     * En modo oscuro, la tienda de noche: el farol se enciende sobre la mesa (el único degradado de la escena).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0A0806), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0806), 0.3f) else x
        // La lona de la tienda, a rayas, con sus pliegues, y la tierra.
        drawRect(c(LONA), Offset.Zero, Size(w, h * 0.62f))
        var x = 0f; var k = 0
        while (x < w) { if (k % 2 == 0) drawRect(c(LONA_RAYA), Offset(x, 0f), Size(14 * u, h * 0.62f)); x += 14 * u; k++ }
        for (j in 0 until 5) drawRect(Color.Black.copy(alpha = 0.07f), Offset(w * (0.1f + j * 0.2f), 0f), Size(18 * u, h * 0.62f))
        drawRect(c(TIERRA), Offset(0f, h * 0.62f), Size(w, h * 0.38f))
        // Las patas en tijera de la mesa plegable.
        listOf(w * 0.14f to w * 0.3f, w * 0.7f to w * 0.86f).forEach { (x0, x1) ->
            drawLine(c(CAOBA_VETA), Offset(x0, h * 0.9f), Offset(x1, h * 1.02f), 4 * u)
            drawLine(c(CAOBA), Offset(x1, h * 0.9f), Offset(x0, h * 1.02f), 4 * u)
            drawCircle(c(LATON), 2.4f * u, Offset((x0 + x1) / 2, h * 0.96f))
        }
        // El tablero de caoba con su paño verde y el canto con su fleje de latón.
        val tab = Rect(w * 0.02f, h * 0.44f, w * 0.98f, h * 0.88f)
        drawRect(c(CAOBA), Offset(tab.left - 4 * u, tab.top - 4 * u), Size(tab.width + 8 * u, tab.height + 4 * u))
        drawRect(c(TAPETE), tab.topLeft, Size(tab.width, tab.height - 6 * u))
        for (j in 0 until 30) drawLine(Color.Black.copy(alpha = 0.04f), Offset(tab.left, tab.top + j * 4 * u), Offset(tab.right, tab.top + j * 4 * u), 1 * u)
        drawRect(c(ORO).copy(alpha = 0.7f), tab.deflate(4 * u).topLeft, Size(tab.width - 8 * u, tab.height - 14 * u), style = Stroke(0.8f * u))
        drawRect(c(CAOBA_LUZ), Offset(tab.left - 4 * u, tab.bottom - 6 * u), Size(tab.width + 8 * u, 9 * u))
        drawLine(c(LATON), Offset(tab.left - 4 * u, tab.bottom + 1 * u), Offset(tab.right + 4 * u, tab.bottom + 1 * u), 1.4f * u)
        // El mapa abierto, debajo del legajo, con sus curvas de nivel y un camino.
        val mapa = Rect(w * 0.24f, h * 0.47f, w * 0.88f, h * 0.84f)
        rotate(-4f, mapa.center) {
            drawRect(c(PERGAMINO), mapa.topLeft, mapa.size)
            listOf(0.33f, 0.66f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.08f), Offset(mapa.left + mapa.width * f, mapa.top), Offset(mapa.left + mapa.width * f, mapa.bottom), 1 * u) }
            for (j in 0 until 3) drawOval(c(TIERRA).copy(alpha = 0.5f), Offset(mapa.right - 70 * u + j * 8 * u, mapa.bottom - 40 * u + j * 5 * u), Size(56 * u - j * 16 * u, 30 * u - j * 9 * u), style = Stroke(0.7f * u))
            drawPath(Path().apply { moveTo(mapa.right - 4 * u, mapa.top + 20 * u); quadraticTo(mapa.right - 40 * u, mapa.center.y, mapa.right - 10 * u, mapa.bottom - 6 * u) },
                c(TEJUELO).copy(alpha = 0.6f), style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))))
        }
        // El catalejo, a la izquierda: los tubos de latón y la funda de cuero.
        rotate(-24f, Offset(w * 0.1f, h * 0.72f)) {
            val y0 = h * 0.72f
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.0f, y0 + 6 * u), Size(w * 0.2f, 9 * u))
            drawRect(c(CUERO), Offset(w * 0.0f, y0 - 6 * u), Size(w * 0.11f, 12 * u))
            for (j in 0 until 4) drawLine(Color.Black.copy(alpha = 0.2f), Offset(w * 0.01f + j * 7 * u, y0 - 6 * u), Offset(w * 0.01f + j * 7 * u, y0 + 6 * u), 0.8f * u)
            drawRect(c(LATON), Offset(w * 0.11f, y0 - 4.5f * u), Size(w * 0.07f, 9 * u))
            drawRect(c(LATON_LUZ), Offset(w * 0.11f, y0 - 3.5f * u), Size(w * 0.07f, 2 * u))
            drawRect(c(LATON_OSC), Offset(w * 0.18f, y0 - 3.5f * u), Size(w * 0.05f, 7 * u))
            listOf(w * 0.11f, w * 0.18f).forEach { rx -> drawRect(c(LATON_OSC), Offset(rx - 1 * u, y0 - 6 * u), Size(2.5f * u, 12 * u)) }
            drawOval(c(Color(0xFF9DB4BF)), Offset(w * 0.228f, y0 - 3 * u), Size(3 * u, 6 * u))
        }
        // A la derecha, el tintero con la pluma de ganso y el farol de campaña.
        val tin = Offset(w * 0.9f, h * 0.73f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(tin.x - 13 * u, tin.y + 6 * u), Size(28 * u, 8 * u))
        drawRect(c(Color(0xFF2A2420)), Offset(tin.x - 10 * u, tin.y - 8 * u), Size(20 * u, 16 * u))
        drawOval(c(LATON), Offset(tin.x - 10 * u, tin.y - 11 * u), Size(20 * u, 6 * u))
        drawOval(c(Color(0xFF0E0A08)), Offset(tin.x - 5 * u, tin.y - 9.5f * u), Size(10 * u, 3 * u))
        val pluma = Path().apply { moveTo(tin.x - 1 * u, tin.y - 9 * u); quadraticTo(tin.x - 16 * u, tin.y - 34 * u, tin.x - 30 * u, tin.y - 62 * u)
            quadraticTo(tin.x - 12 * u, tin.y - 44 * u, tin.x - 1 * u, tin.y - 9 * u); close() }
        drawPath(pluma, c(PERGAMINO))
        drawPath(Path().apply { moveTo(tin.x - 1 * u, tin.y - 9 * u); quadraticTo(tin.x - 15 * u, tin.y - 36 * u, tin.x - 30 * u, tin.y - 62 * u) }, c(TIERRA), style = Stroke(0.7f * u))
        val far = Offset(w * 0.86f, h * 0.36f)
        drawLine(c(LATON_OSC), Offset(far.x, far.y - 26 * u), Offset(far.x, far.y - 20 * u), 1.4f * u)
        drawArc(c(LATON_OSC), 180f, 180f, false, Offset(far.x - 5 * u, far.y - 30 * u), Size(10 * u, 8 * u), style = Stroke(1.2f * u))
        drawPath(Path().apply { moveTo(far.x - 9 * u, far.y - 18 * u); lineTo(far.x + 9 * u, far.y - 18 * u); lineTo(far.x, far.y - 25 * u); close() }, c(LATON))
        drawRect(if (oscuro) FAROL.copy(alpha = 0.75f) else c(PERGAMINO).copy(alpha = 0.6f), Offset(far.x - 8 * u, far.y - 18 * u), Size(16 * u, 26 * u))
        drawCircle(if (oscuro) Color(0xFFFFF0C0) else c(FAROL).copy(alpha = 0.5f), 3 * u, Offset(far.x, far.y - 4 * u))
        listOf(-8f, 0f, 8f).forEach { dx -> drawLine(c(LATON_OSC), Offset(far.x + dx * u, far.y - 18 * u), Offset(far.x + dx * u, far.y + 8 * u), 1 * u) }
        drawRect(c(LATON), Offset(far.x - 10 * u, far.y + 8 * u), Size(20 * u, 4 * u))
        if (oscuro) drawRect(Brush.radialGradient(listOf(FAROL.copy(alpha = 0.36f), FAROL.copy(alpha = 0.12f), Color.Transparent), Offset(far.x, far.y - 4 * u), w * 0.8f))
        // El legajo.
        val ancho = w * 0.56f; val alto = minOf(ancho * 0.64f, h * 0.5f)
        val r = Rect(w * 0.2f, h * 0.36f, w * 0.2f + ancho, h * 0.36f + alto)
        val g = (3f + 13f * lleno) * u
        val lomo = r.left + ancho * 0.06f
        val tafilete = TAFILETES[2]
        rotate(2f, r.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), r.topLeft + Offset(5 * u, 5 * u + g), r.size)
            // Los despachos doblados que asoman por arriba, cada uno con su lacre.
            val n = (1 + e.hojas / 10).coerceAtMost(9)
            for (j in 0 until n) {
                val dx = r.left + ancho * (0.09f + 0.095f * j)
                val sale = (8f + 10f * azar(e.semilla, 90 + j)) * u * (1f - 0.5f * abre)
                rotate(-7f + 14f * azar(e.semilla, 100 + j), Offset(dx + ancho * 0.06f, r.top)) {
                    drawRect(Color.Black.copy(alpha = 0.12f), Offset(dx + 1 * u, r.top - sale + 1 * u), Size(ancho * 0.12f, 30 * u))
                    drawRect(c(DESPACHO), Offset(dx, r.top - sale), Size(ancho * 0.12f, 30 * u))
                    drawLine(c(TINTA).copy(alpha = 0.2f), Offset(dx, r.top - sale + 9 * u), Offset(dx + ancho * 0.12f, r.top - sale + 9 * u), 0.6f * u)
                    if (j % 2 == 0) drawCircle(c(LACRE), 2.6f * u, Offset(dx + ancho * 0.06f, r.top - sale + 4.5f * u))
                }
            }
            // Los despachos apilados, vistos por el canto, y la tapa de atrás.
            drawRect(c(mezcla(tafilete, Color.Black, 0.3f)), r.topLeft + Offset(0f, g), r.size)
            cantoLibreta(r, g * 0.85f, c(DESPACHO), c(TINTA).copy(alpha = 0.2f))
            if (g > 6 * u) for (j in 0 until (e.hojas / 30).coerceAtMost(5)) drawCircle(c(LACRE), 1.6f * u, Offset(r.left + ancho * (0.2f + 0.15f * j), r.bottom + g * (0.3f + 0.4f * azar(e.semilla, 110 + j))))
            // Por dentro: el despacho de encima, con su encabezado, sus renglones y su sello.
            if (abre > 0.01f) {
                val hoja = Rect(lomo, r.top, r.right, r.bottom)
                drawRect(cp(DESPACHO), hoja.topLeft, hoja.size)
                drawLine(c(TINTA).copy(alpha = 0.18f), Offset(hoja.left, hoja.center.y), Offset(hoja.right, hoja.center.y), 0.8f * u)
                textoEn(medidor, "Ordre du jour", Rect(hoja.left + 20 * u, hoja.top + 8 * u, hoja.right - 20 * u, hoja.top + 24 * u), LetraExtra.bodoni, c(TINTA), 12f)
                for (j in 0 until 6) {
                    val ly = hoja.top + 34 * u + j * 10 * u
                    if (ly < hoja.bottom - 20 * u) drawLine(c(TINTA).copy(alpha = 0.5f), Offset(hoja.left + 16 * u, ly), Offset(hoja.right - (if (j % 3 == 2) 60 else 16) * u, ly), 0.8f * u)
                }
                val s = Offset(hoja.right - 26 * u, hoja.bottom - 20 * u)
                drawCircle(c(LACRE), 10 * u, s); drawCircle(c(mezcla(LACRE, Color.White, 0.2f)), 7.5f * u, s, style = Stroke(1 * u))
            }
            // La tapa de tafilete, que gira sobre el lomo.
            val sx = 1f - abre
            if (sx > 0.02f) scale(sx, 1f, Offset(lomo, r.center.y)) {
                drawRect(c(tafilete), r.topLeft, r.size)
                for (j in 0 until 40) drawCircle(Color.Black.copy(alpha = 0.06f), (0.6f + azar(e.semilla, 120 + j)) * u, Offset(r.left + ancho * azar(121, j), r.top + alto * azar(122, j)))
                drawRect(c(ORO), r.deflate(6 * u).topLeft, r.deflate(6 * u).size, style = Stroke(1.2f * u))
                drawRect(c(ORO).copy(alpha = 0.6f), r.deflate(9 * u).topLeft, r.deflate(9 * u).size, style = Stroke(0.6f * u))
                listOf(r.deflate(6 * u).topLeft, Offset(r.right - 6 * u, r.top + 6 * u), Offset(r.left + 6 * u, r.bottom - 6 * u), Offset(r.right - 6 * u, r.bottom - 6 * u))
                    .forEach { drawPath(estrella(it, 4 * u, 4, 0.35f, 45f), c(ORO)) }
                // La cartela de pergamino con el nombre y la cuenta.
                val ca = Rect(r.left + ancho * 0.13f, r.top + alto * 0.14f, r.left + ancho * 0.74f, r.top + alto * 0.52f)
                drawRect(Color.Black.copy(alpha = 0.25f), ca.topLeft + Offset(1.2f * u, 1.6f * u), ca.size)
                drawRect(cp(PERGAMINO), ca.topLeft, ca.size)
                drawRect(c(TEJUELO).copy(alpha = 0.7f), ca.deflate(3 * u).topLeft, ca.deflate(3 * u).size, style = Stroke(0.8f * u))
                textoEn(medidor, e.nombre, Rect(ca.left + 5 * u, ca.top + 5 * u, ca.right - 5 * u, ca.top + ca.height * 0.58f), letra, c(TINTA), 14f, peso = FontWeight.Bold, minimo = 5.5f)
                textoEn(medidor, e.cuenta, Rect(ca.left + 10 * u, ca.top + ca.height * 0.58f, ca.right - 10 * u, ca.bottom - 5 * u), LetraExtra.bodoni, c(TEJUELO), 10f, minimo = 6f)
                if (abre > 0f) drawRect(Color.Black.copy(alpha = 0.45f * abre), r.topLeft, r.size)
            }
            if (abre > 0.01f && sx > 0.02f) drawRect(Color.Black.copy(alpha = 0.16f), Offset(lomo + (r.right - lomo) * sx, r.top), Size(14 * u * sin(PI.toFloat() * abre), r.height))
            drawRect(c(mezcla(tafilete, Color.Black, 0.35f)), r.topLeft, Size(lomo - r.left, r.height))
            listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawLine(c(ORO).copy(alpha = 0.7f), Offset(r.left, r.top + alto * f), Offset(lomo, r.top + alto * f), 1 * u) }
            // El cordón rojo en cruz con el sello de lacre del laurel: al abrir se suelta.
            val queda = 1f - suelta
            if (queda > 0f) {
                val cs = Offset(r.left + ancho * 0.86f, r.top + alto * 0.72f)
                val flojo = suelta * 10 * u
                drawLine(c(CORDON).copy(alpha = queda), Offset(r.left - 2 * u, cs.y + flojo), Offset(r.right + 3 * u, cs.y - flojo), 2.6f * u)
                drawLine(c(CORDON).copy(alpha = queda), Offset(cs.x - flojo, r.top - 2 * u), Offset(cs.x + flojo, r.bottom + g), 2.6f * u)
                drawLine(c(CORDON).copy(alpha = queda), cs, cs + Offset(10 * u, 16 * u + flojo), 2 * u)
                drawLine(c(CORDON).copy(alpha = queda), cs, cs + Offset(-6 * u, 18 * u + flojo), 2 * u)
                val R = alto * 0.12f
                drawCircle(Color.Black.copy(alpha = 0.25f * queda), R, cs + Offset(1.5f * u, 2 * u))
                drawCircle(c(LACRE).copy(alpha = queda), R, cs)
                drawCircle(c(mezcla(LACRE, Color.White, 0.18f)).copy(alpha = queda), R * 0.8f, cs, style = Stroke(1.1f * u))
                ramaLaurel(Offset(cs.x, cs.y + R * 0.45f), R * 0.7f, -1f, c(ORO).copy(alpha = 0.85f * queda))
                ramaLaurel(Offset(cs.x, cs.y + R * 0.45f), R * 0.7f, 1f, c(ORO).copy(alpha = 0.85f * queda))
            }
        }
    }

    /**
     * La pestaña del día es un tejuelo de tafilete rojo con su doble filete dorado: la letra de la semana arriba, el día
     * grande dorado y, abajo, el sello de lacre en el color del ánimo (sin ánimo, una abeja dorada pequeña en su lugar).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size)
        val cuero = if ((e.semilla and 4) == 0) TEJUELO else TAFILETES[(e.semilla and 0x7fffffff) % 3 + 1]
        drawRect(c(cuero), r.topLeft, r.size)
        drawRect(c(ORO), r.deflate(2.5f * u).topLeft, r.deflate(2.5f * u).size, style = Stroke(0.9f * u))
        drawRect(c(ORO).copy(alpha = 0.6f), r.deflate(4.5f * u).topLeft, r.deflate(4.5f * u).size, style = Stroke(0.5f * u))
        textoEn(medidor, e.semana, Rect(r.left + 6 * u, r.top + 5 * u, r.right - 6 * u, r.top + 14 * u), letra, c(ORO), 8f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 4 * u, r.top + 14 * u, r.right - 4 * u, r.top + 33 * u), letra, c(ORO), 18f, peso = FontWeight.Bold, minimo = 9f)
        val s = Offset(r.center.x, r.bottom - 10 * u)
        if (e.tono != null) {
            drawCircle(mezcla(e.tono, Color.Black, 0.35f), 6.6f * u, s)
            drawCircle(e.tono, 5.4f * u, s)
            drawCircle(mezcla(e.tono, Color.White, 0.3f), 3.6f * u, s, style = Stroke(0.7f * u))
        } else {
            drawOval(c(ORO), Offset(s.x - 1.8f * u, s.y - 3 * u), Size(3.6f * u, 6 * u))
            drawOval(c(ORO).copy(alpha = 0.6f), Offset(s.x - 5 * u, s.y - 3.5f * u), Size(4 * u, 2.6f * u))
            drawOval(c(ORO).copy(alpha = 0.6f), Offset(s.x + 1 * u, s.y - 3.5f * u), Size(4 * u, 2.6f * u))
        }
    }

    /** El mes dorado en una banda de tafilete rojo con su filete, entre dos ramitas de laurel. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(8 * u, 4 * u, w - 8 * u, h - 4 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), b.topLeft + Offset(1.2f * u, 1.6f * u), b.size)
        drawRect(c(TEJUELO), b.topLeft, b.size)
        drawRect(c(ORO), b.deflate(2.5f * u).topLeft, b.deflate(2.5f * u).size, style = Stroke(0.9f * u))
        val medida = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5f.sp)).size.width
        val mitad = minOf(medida / 2f, b.width / 2 - 40 * u)
        ramaLaurel(Offset(b.center.x - mitad - 8 * u, b.center.y + 5 * u), 22 * u, -1f, c(ORO))
        ramaLaurel(Offset(b.center.x + mitad + 8 * u, b.center.y + 5 * u), 22 * u, 1f, c(ORO))
        textoEn(medidor, texto, Rect(b.left + 40 * u, b.top + 3 * u, b.right - 40 * u, b.bottom - 3 * u), letra, c(ORO), 13f, peso = FontWeight.Bold, espaciado = 1.5f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El azul, el blanco y el rojo de la cinta del despacho. */
    private val AZUL_CINTA = Color(0xFF1E3A8A)
    private val BLANCO_CINTA = Color(0xFFF4EFE2)
    private val ROJO_CINTA = Color(0xFFA3172E)

    /** El águila de las armas, de alas abiertas sobre el rayo, en [c] con radio [r]. */
    private fun DrawScope.aguila(c: Offset, r: Float, color: Color) {
        listOf(-1f, 1f).forEach { s ->
            val ala = Path().apply {
                moveTo(c.x + s * r * 0.08f, c.y - r * 0.12f)
                quadraticTo(c.x + s * r * 0.5f, c.y - r * 0.75f, c.x + s * r * 0.9f, c.y - r * 0.52f)
                lineTo(c.x + s * r * 0.74f, c.y - r * 0.36f); lineTo(c.x + s * r * 0.84f, c.y - r * 0.24f)
                lineTo(c.x + s * r * 0.66f, c.y - r * 0.12f); lineTo(c.x + s * r * 0.74f, c.y + r * 0.02f)
                quadraticTo(c.x + s * r * 0.4f, c.y + r * 0.12f, c.x + s * r * 0.1f, c.y + r * 0.2f); close()
            }
            drawPath(ala, color)
        }
        drawOval(color, Offset(c.x - r * 0.15f, c.y - r * 0.3f), Size(r * 0.3f, r * 0.66f))
        drawCircle(color, r * 0.12f, Offset(c.x - r * 0.03f, c.y - r * 0.4f))
        drawPath(Path().apply { moveTo(c.x - r * 0.12f, c.y - r * 0.44f); lineTo(c.x - r * 0.26f, c.y - r * 0.38f); lineTo(c.x - r * 0.12f, c.y - r * 0.34f); close() }, color)
        drawPath(Path().apply { moveTo(c.x - r * 0.14f, c.y + r * 0.3f); lineTo(c.x, c.y + r * 0.62f); lineTo(c.x + r * 0.14f, c.y + r * 0.3f); close() }, color)
        drawPath(Path().apply { moveTo(c.x - r * 0.5f, c.y + r * 0.42f); lineTo(c.x - r * 0.15f, c.y + r * 0.34f); lineTo(c.x + r * 0.05f, c.y + r * 0.46f); lineTo(c.x + r * 0.5f, c.y + r * 0.36f) },
            color, style = Stroke(r * 0.07f))
    }

    /** El lacre del águila: una gota roja de borde chorreado, el aro de laurel y el águila en relieve. */
    private fun DrawScope.lacreAguila(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val gota = Path().apply {
            for (i in 0 until 18) {
                val a = (2 * PI * i / 18).toFloat()
                val rr = r * (if (i % 5 == 2) 1.12f else 0.95f + 0.06f * azar(i, 61))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        translate(r * 0.06f, r * 0.1f) { drawPath(gota, Color.Black.copy(alpha = 0.3f * alfa)) }
        drawPath(gota, k(LACRE))
        drawCircle(k(mezcla(LACRE, Color.Black, 0.3f)), r * 0.76f, c, style = Stroke(r * 0.07f))
        for (i in 0 until 14) {
            val a = (PI * 0.15 + 2 * PI * i / 14).toFloat()
            val p = Offset(c.x + cos(a) * r * 0.66f, c.y + sin(a) * r * 0.66f)
            drawOval(k(mezcla(LACRE, ORO, 0.35f)), Offset(p.x - r * 0.07f, p.y - r * 0.035f), Size(r * 0.14f, r * 0.07f))
        }
        aguila(c, r * 0.5f, k(mezcla(LACRE, ORO, 0.3f)))
        drawArc(k(Color.White.copy(alpha = 0.3f)), 200f, 60f, false, Offset(c.x - r * 0.82f, c.y - r * 0.82f), Size(r * 1.64f, r * 1.64f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    /**
     * El pliego de despacho de campaña: la cubierta de papel verjurado, doblada en sobre a mano, con su orilla barbada, el
     * filete dorado y la solapa en pico que baja hasta el medio, donde la cierra el lacre rojo del águila. En la solapa, la
     * N coronada entre dos ramas de laurel; abajo, a pluma, a quién va y las fechas, y el cuño rojo de franquicia «GRANDE
     * ARMÉE». Las de al futuro van además atadas con la cinta tricolor, que se anuda a un lado con sus colas en pico. Al
     * abrirlo, el lacre se parte, la solapa se levanta mostrando el reverso escrito y sube el boletín. En modo oscuro, el
     * papel queda en la penumbra de la tienda; la tinta, azul de Prusia, se sigue leyendo.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val tinta = Color(0xFF14213D)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(l + 1.5f * u, top + 2.5f * u), b.size)
        // El reverso de la cubierta, por dentro, con la letra de otro despacho que se transparenta.
        drawRect(c(mezcla(DESPACHO, PERGAMINO, 0.5f)), b.topLeft, b.size)
        for (j in 0 until 6) drawLine(c(tinta).copy(alpha = 0.14f), Offset(l + W * 0.12f, top + H * (0.07f + j * 0.06f)), Offset(r - W * (0.12f + 0.2f * azar(e.semilla, j)), top + H * (0.07f + j * 0.06f)), 0.8f * u)
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val punta = H * 0.42f
        val solapa = Path().apply {
            moveTo(l, top); lineTo(r, top); lineTo(cx + W * 0.06f, top + punta - 3 * u); quadraticTo(cx, top + punta + 1 * u, cx - W * 0.06f, top + punta - 3 * u); close()
        }
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            drawPath(solapa, c(mezcla(DESPACHO, PERGAMINO, 0.6f)))
            clipPath(solapa) { for (j in 0 until 5) drawLine(c(tinta).copy(alpha = 0.2f), Offset(l + W * 0.15f, top + H * (0.06f + j * 0.07f)), Offset(r - W * 0.15f, top + H * (0.06f + j * 0.07f)), 0.8f * u) }
            drawPath(solapa, c(PERGAMINO), style = Stroke(0.8f * u))
        }
        // El boletín.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.82f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 9 * u, 5.5f * u, 7 * u, oscuro)
            drawLine(c(ORO), Offset(h.left + 3 * u, h.top + 3 * u), Offset(h.right - 3 * u, h.top + 3 * u), 0.6f * u)
            aguila(Offset(h.center.x, h.top + 5.5f * u), 3.5f * u, c(AZUL_CINTA).copy(alpha = 0.6f))
        }
        // El frente: los dos dobleces laterales y el de abajo, con la orilla barbada y el filete dorado.
        val v = top + H * 0.3f
        val frente = Path().apply {
            moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f); lineTo(r, bot); lineTo(l, bot); close()
        }
        drawPath(frente, c(DESPACHO))
        clipPath(frente) {
            for (j in 0 until 30) { val x = l + W * j / 30f; drawLine(c(PERGAMINO).copy(alpha = 0.6f), Offset(x, top), Offset(x, bot), 0.5f * u) }
            drawRect(c(ORO).copy(alpha = 0.85f), Offset(l + 3 * u, top), Size(W - 6 * u, H - 3 * u), style = Stroke(0.7f * u))
            drawLine(c(PERGAMINO), Offset(l, bot), Offset(cx - W * 0.08f, v + H * 0.06f), 1 * u)
            drawLine(c(PERGAMINO), Offset(r, bot), Offset(cx + W * 0.08f, v + H * 0.06f), 1 * u)
            // El cuño de franquicia.
            val cu = Offset(r - W * 0.13f, top + H * 0.4f)
            rotate(-12f, cu) {
                drawCircle(c(ROJO_CINTA).copy(alpha = 0.5f), H * 0.1f, cu, style = Stroke(0.8f * u))
                drawCircle(c(ROJO_CINTA).copy(alpha = 0.5f), H * 0.075f, cu, style = Stroke(0.5f * u))
                textoEn(medidor, "G·A", Rect(cu.x - H * 0.08f, cu.y - H * 0.05f, cu.x + H * 0.08f, cu.y + H * 0.05f), letra, c(ROJO_CINTA).copy(alpha = 0.6f), 6f, minimo = 3f)
            }
        }
        drawPath(Path().apply { moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f) }, c(PERGAMINO), style = Stroke(1.2f * u))
        // La solapa en pico, cerrada, con la N coronada entre laureles.
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            translate(0f, 1.5f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.14f)) }
            drawPath(solapa, c(mezcla(DESPACHO, BLANCO_CINTA, 0.4f)))
            clipPath(solapa) {
                for (j in 0 until 30) { val x = l + W * j / 30f; drawLine(c(PERGAMINO).copy(alpha = 0.5f), Offset(x, top), Offset(x, top + punta), 0.5f * u) }
                val n = Offset(cx, top + punta * 0.34f)
                ramaLaurel(n + Offset(-2 * u, punta * 0.12f), punta * 0.32f, -1f, c(ORO))
                ramaLaurel(n + Offset(2 * u, punta * 0.12f), punta * 0.32f, 1f, c(ORO))
                textoEn(medidor, "N", Rect(n.x - punta * 0.14f, n.y - punta * 0.12f, n.x + punta * 0.14f, n.y + punta * 0.18f), letra, c(ORO), punta * 0.26f / u, peso = FontWeight.SemiBold, minimo = 4f)
            }
            drawPath(solapa, c(ORO).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        }
        // La cinta tricolor, a lo ancho, anudada a un lado.
        val y0 = top + punta
        val selloR = H * 0.14f
        if (e.cordon > 0f) {
            val ancho = 6 * u
            val hasta = l + (W + 2 * u) * e.cordon.coerceAtMost(0.85f) / 0.85f
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(l, y0 - ancho / 2 + 1.5f * u), Size(hasta - l, ancho))
            listOf(AZUL_CINTA, BLANCO_CINTA, ROJO_CINTA).forEachIndexed { i, col -> drawRect(c(col), Offset(l, y0 - ancho / 2 + i * ancho / 3), Size(hasta - l, ancho / 3)) }
            val nudo = ((e.cordon - 0.8f) / 0.2f).coerceIn(0f, 1f)
            if (nudo > 0f) {
                val n0 = Offset(cx + selloR * 1.8f, y0)
                scale(nudo, nudo, n0) {
                    listOf(-1f, 1f).forEach { s ->
                        val cola = Path().apply { moveTo(n0.x, n0.y); lineTo(n0.x + s * 5 * u, n0.y + 12 * u); lineTo(n0.x + s * 3 * u, n0.y + 10 * u); lineTo(n0.x + s * 1.5f * u, n0.y + 13 * u); close() }
                        drawPath(cola, c(if (s < 0) AZUL_CINTA else ROJO_CINTA))
                        drawOval(c(if (s < 0) AZUL_CINTA else ROJO_CINTA), Offset(n0.x + (if (s < 0) -8f else 0f) * u, n0.y - 3.5f * u), Size(8 * u, 6 * u))
                    }
                    drawCircle(c(BLANCO_CINTA), 2.4f * u, n0)
                    drawCircle(c(ROJO_CINTA), 2.4f * u, n0, style = Stroke(0.6f * u))
                }
            }
        }
        // El lacre del águila.
        val selloC = Offset(cx, top + punta * k)
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(FAROL), e.brillo)
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, selloC.y - y0 - selloR * 0.3f * e.romper),
                Offset(selloR * 0.25f * e.romper, selloR * 0.3f * e.romper), e.semilla) { lacreAguila(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.25f, selloR * 0.3f), e.semilla, conArriba = false) { lacreAguila(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        }
        textoEn(medidor, e.linea1, Rect(l + W * 0.1f, top + H * 0.62f, r - W * 0.06f, top + H * 0.78f), LetraExtra.bodoni, c(tinta), 11.5f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(l + W * 0.1f, top + H * 0.79f, r - W * 0.06f, top + H * 0.91f), LetraExtra.bodoni, c(tinta).copy(alpha = 0.8f), 9f, minimo = 6f)
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.24f, 0.84f, 0.39f)

    override val altoBuzon: Dp = 200.dp

    /** Un despacho chico, de pie en la valija: doblado con su lacre (que brilla si está listo), atado con la cinta o abierto. */
    private fun DrawScope.despachoChico(r: Rect, tipo: Int, oscuro: Boolean, inclina: Float) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        rotate(inclina, Offset(r.center.x, r.bottom)) {
            if (tipo == 2) {
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1 * u, r.top - r.height * 0.4f + 1 * u), Size(r.width, r.height * 1.4f))
                drawRect(c(DESPACHO), Offset(r.left, r.top - r.height * 0.4f), Size(r.width, r.height * 1.4f))
                for (j in 0 until 5) drawLine(c(TINTA).copy(alpha = 0.35f), Offset(r.left + r.width * 0.15f, r.top - r.height * 0.28f + j * r.height * 0.16f), Offset(r.right - r.width * 0.15f, r.top - r.height * 0.28f + j * r.height * 0.16f), 0.6f * u)
                drawLine(c(PERGAMINO), Offset(r.left, r.top + r.height * 0.3f), Offset(r.right, r.top + r.height * 0.3f), 0.8f * u)
                drawArc(c(LACRE), 0f, 180f, true, Offset(r.center.x - r.width * 0.14f, r.top + r.height * 0.3f - r.width * 0.14f), Size(r.width * 0.28f, r.width * 0.28f))
                return@rotate
            }
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1 * u, 1 * u), r.size)
            drawRect(c(DESPACHO), r.topLeft, r.size)
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.center.x, r.top + r.height * 0.5f); close() }, c(mezcla(DESPACHO, BLANCO_CINTA, 0.5f)))
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, r.top + r.height * 0.5f); lineTo(r.right, r.top) }, c(PERGAMINO), style = Stroke(0.7f * u))
            if (tipo == 1) listOf(AZUL_CINTA, BLANCO_CINTA, ROJO_CINTA).forEachIndexed { i, col -> drawRect(c(col), Offset(r.left, r.top + r.height * 0.45f + i * 1.2f * u), Size(r.width, 1.2f * u)) }
            drawCircle(c(LACRE), r.width * 0.15f, Offset(r.center.x, r.top + r.height * 0.5f))
            drawCircle(c(mezcla(LACRE, ORO, 0.3f)), r.width * 0.07f, Offset(r.center.x, r.top + r.height * 0.5f), style = Stroke(0.6f * u))
        }
    }

    /**
     * El portamanteau del correo de campaña, tendido sobre su caballete de tijera delante del paño verde del puesto de
     * postas: un cilindro de cuero con sus tres correas de hebilla de latón, las tapas redondas de los extremos con el aro
     * de latón y la N, y la boca larga de arriba abierta, con la tapa doblada hacia atrás. De la boca asoman los despachos
     * (hasta diez): los que ya se pueden abrir, con el lacre que brilla a la luz del farol (el primer degradado); los
     * lacrados, atados con su cinta tricolor; los abiertos, desplegados. En el cuero, tres placas de latón remachadas con la
     * cuenta de cada clase. Abajo, la alfombra de campaña. En modo oscuro, la tienda de noche bajo el farol (el segundo
     * degradado).
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF080706), 0.35f) else t(x, false, false)
        // El paño verde del puesto, con su orla de clavos dorados.
        drawRect(m(TAPETE))
        for (j in 0 until 40) { val x = w * azar(101, j); drawLine(m(mezcla(TAPETE, Color.Black, 0.2f)).copy(alpha = 0.4f), Offset(x, 0f), Offset(x + 3 * u, h * 0.8f), 0.6f * u) }
        var x = 6 * u; while (x < w) { drawCircle(m(LATON), 1.5f * u, Offset(x, 6 * u)); x += 10 * u }
        if (oscuro) mancha(Offset(w * 0.88f, h * 0.1f), w * 0.55f, c(FAROL).copy(alpha = 0.22f))
        // El farol colgado arriba a la derecha.
        val fa = Offset(w * 0.9f, h * 0.06f)
        drawLine(m(HIERRO_FAROL), Offset(fa.x, 0f), fa, 1 * u)
        drawRect(c(LATON_OSC), Offset(fa.x - 6 * u, fa.y), Size(12 * u, 3 * u))
        drawRect(if (oscuro) FAROL else c(PERGAMINO), Offset(fa.x - 5 * u, fa.y + 3 * u), Size(10 * u, 14 * u))
        drawRect(c(LATON_OSC), Offset(fa.x - 5 * u, fa.y + 3 * u), Size(10 * u, 14 * u), style = Stroke(1 * u))
        drawRect(c(LATON_OSC), Offset(fa.x - 6 * u, fa.y + 17 * u), Size(12 * u, 3 * u))
        // La alfombra.
        val piso = h * 0.88f
        drawRect(m(ALFOMBRA), Offset(0f, piso), Size(w, h - piso))
        drawRect(m(ALFOMBRA_ORLA), Offset(0f, piso + 3 * u), Size(w, 3 * u))
        // El caballete de tijera.
        val cil = Rect(w * 0.07f, h * 0.36f, w * 0.93f, h * 0.76f)
        listOf(cil.left + cil.width * 0.16f, cil.right - cil.width * 0.16f).forEach { px ->
            drawLine(m(CAOBA_VETA), Offset(px - 14 * u, piso + 2 * u), Offset(px + 10 * u, cil.center.y), 4 * u, StrokeCap.Round)
            drawLine(m(CAOBA), Offset(px + 14 * u, piso + 2 * u), Offset(px - 10 * u, cil.center.y), 4 * u, StrokeCap.Round)
            drawCircle(c(LATON), 2 * u, Offset(px, cil.center.y + (piso - cil.center.y) * 0.45f))
        }
        // Los despachos que asoman por la boca, detrás del labio del cuero.
        val boca = Rect(cil.left + cil.width * 0.1f, cil.top - 4 * u, cil.right - cil.width * 0.1f, cil.top + 6 * u)
        val tapaAtras = Path().apply { moveTo(boca.left, boca.top + 3 * u); lineTo(boca.left + 6 * u, boca.top - 16 * u); lineTo(boca.right - 6 * u, boca.top - 16 * u); lineTo(boca.right, boca.top + 3 * u); close() }
        drawPath(tapaAtras, c(CUERO_OSC))
        drawPath(tapaAtras, c(CUERO_LUZ).copy(alpha = 0.5f), style = Stroke(1 * u))
        drawRoundRect(c(CUERO_OSC), boca.topLeft, boca.size, CornerRadius(5 * u))
        val cartas = cartasBuzonCo(e, 10)
        val paso = boca.width / 10
        cartas.forEachIndexed { i, tipo ->
            val dw = paso * 0.8f; val dh = 28 * u
            val dx = boca.left + paso * (i + 0.5f)
            val r0 = Rect(dx - dw / 2, boca.bottom - dh - 2 * u - azar(102, i) * 6 * u, dx + dw / 2, boca.bottom + 4 * u)
            if (tipo == 0) mancha(Offset(dx, r0.top + dh * 0.45f), paso * 0.9f, c(FAROL).copy(alpha = 0.55f))
            despachoChico(r0, tipo, oscuro, (azar(103, i) - 0.5f) * 10f)
        }
        // El cilindro de cuero.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), cil.topLeft + Offset(3 * u, 4 * u), cil.size, CornerRadius(cil.height / 2.4f))
        drawRoundRect(c(CUERO), Offset(cil.left, cil.top + 4 * u), Size(cil.width, cil.height - 4 * u), CornerRadius(cil.height / 2.4f))
        drawRect(c(CUERO_LUZ).copy(alpha = 0.5f), Offset(cil.left + cil.height * 0.4f, cil.top + cil.height * 0.22f), Size(cil.width - cil.height * 0.8f, 3 * u))
        drawRect(c(CUERO_OSC).copy(alpha = 0.6f), Offset(cil.left + cil.height * 0.4f, cil.bottom - cil.height * 0.2f), Size(cil.width - cil.height * 0.8f, cil.height * 0.16f))
        var sx = cil.left + 10 * u; while (sx < cil.right - 10 * u) { drawLine(c(CUERO_OSC), Offset(sx, cil.top + 7 * u), Offset(sx + 2 * u, cil.top + 7 * u), 0.7f * u); sx += 4 * u }
        // Las tapas de los extremos.
        listOf(cil.left + cil.height * 0.22f, cil.right - cil.height * 0.22f).forEach { ex ->
            val tc = Rect(ex - cil.height * 0.22f, cil.top + 4 * u, ex + cil.height * 0.22f, cil.bottom)
            drawOval(c(CUERO_OSC), tc.topLeft, tc.size)
            drawOval(c(LATON), tc.topLeft, tc.size, style = Stroke(2.2f * u))
            textoEn(medidor, "N", tc.deflate(tc.width * 0.22f), letra, c(ORO), 12f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        // Las correas con su hebilla.
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f ->
            val cx = cil.left + cil.width * f
            drawRect(c(CUERO_OSC), Offset(cx - 4 * u, cil.top + 2 * u), Size(8 * u, cil.height - 2 * u))
            drawRect(c(CUERO_LUZ).copy(alpha = 0.4f), Offset(cx - 4 * u, cil.top + 2 * u), Size(1.2f * u, cil.height - 2 * u))
            drawRoundRect(c(LATON), Offset(cx - 5.5f * u, cil.bottom - 18 * u), Size(11 * u, 9 * u), CornerRadius(1.5f * u), style = Stroke(1.6f * u))
            drawLine(c(LATON_OSC), Offset(cx, cil.bottom - 17 * u), Offset(cx, cil.bottom - 10 * u), 1 * u)
        }
        // Las tres placas de latón con la cuenta.
        val cuentas = listOf("POR ABRIR" to e.listas, "LACRADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val pw = cil.width * 0.17f
        listOf(0.2f, 0.4f, 0.6f).zip(cuentas).forEachIndexed { i, (f, cu) ->
            val px = cil.left + cil.width * (f + 0.1f) - pw / 2
            val pl = Rect(px + 4 * u, cil.top + cil.height * 0.3f, px + pw - 4 * u, cil.top + cil.height * 0.78f)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(0.8f * u, 1.2f * u), pl.size, CornerRadius(2 * u))
            drawRoundRect(c(LATON), pl.topLeft, pl.size, CornerRadius(2 * u))
            drawRoundRect(c(LATON_OSC), pl.deflate(1.5f * u).topLeft, pl.deflate(1.5f * u).size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
            textoEn(medidor, cu.first, Rect(pl.left + 2 * u, pl.top + 2 * u, pl.right - 2 * u, pl.top + pl.height * 0.42f), letra, c(TINTA), 7.5f, peso = FontWeight.SemiBold, espaciado = 0.4f, minimo = 4.5f)
            textoEn(medidor, "${cu.second}", Rect(pl.left + 2 * u, pl.top + pl.height * 0.38f, pl.right - 2 * u, pl.bottom - 1.5f * u), LetraExtra.bodoni, c(if (i == 0 && cu.second > 0) TEJUELO else TINTA), 14f, peso = FontWeight.SemiBold, minimo = 6f)
        }
    }

    private val HIERRO_FAROL = Color(0xFF2A2622)
}

/**
 * Art déco: el estante del salón de un transatlántico. Arce claro chapeado en libro, con bandas de cromo en los costados y
 * en el canto de cada tabla. Arriba, el remate escalonado en tres gradas con un sol de marquetería, rayos de arce y nogal
 * que salen del ojo de buey de cromo atornillado: de día se ven el cielo y el mar con su horizonte; de noche, el mar oscuro
 * con la luna y su reflejo. Abajo, el zócalo escalonado con sus filetes de cromo y un piso de linóleo en damero. Los meses
 * van en cartelas de laca negra de esquinas cortadas. Cada entrada es un álbum de viaje con lomo geométrico, bandas de cromo
 * arriba y abajo, la cartela con el día, el mes a lo largo, chevrones dorados que se suman cuanto más escribiste y un sol
 * pequeño; de noche, una luna en lugar del sol. Distinto del Ideario, que es el casillero de laca de la recepción de un hotel.
 * La tapa es ese álbum de frente, con el sol de marquetería (de noche, el mar con la luna), la placa «PASAJE», la
 * cartela escalonada y la fecha del pasaje; al abrirlo, se ve la foto del barco en sus esquineros.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.37f, 0.31f, 0.87f, 0.705f)
    override val letraTapa: FontFamily = LetraExtra.pasaje
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = LACA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La crema de la cartela de la tapa: hundida en oscuro y apenas azul de noche, pero clara para el extracto. */
    private fun cremaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = CREMA
        if (oscuro) r = mezcla(r, Color(0xFF0C0C0E), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.12f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val i = (e.semilla and 0x7fffffff) % ALBUMES.size
        val tapa = c(ALBUMES[i])
        val claro = i == 4
        val dorado = c(if (claro) NOGAL else ORO)
        val rotulo = if (claro) c(LACA) else c(CREMA)
        val cu = Rect(w * 0.02f, h * 0.05f, w * 0.97f, h * 0.94f)
        val bisagra = cu.left + w * 0.075f
        drawRoundRect(Color.Black.copy(alpha = 0.22f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(2 * u))
        drawRect(c(CREMA), Offset(cu.left + 3 * u, cu.top + 3 * u), cu.size)
        for (k in 1..3) drawLine(c(ARCE_VETA).copy(alpha = 0.5f), Offset(cu.left + 8 * u, cu.bottom + k * 0.7f * u), Offset(cu.right + 2 * u, cu.bottom + k * 0.7f * u), 0.4f * u)
        // Por dentro (se ve al abrirse): la hoja negra del álbum con una fotografía del barco en sus esquineros y el talón de un pasaje.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(LACA), hoja.topLeft, hoja.size)
            val foto = Rect(hoja.left + hoja.width * 0.1f, hoja.top + hoja.height * 0.14f, hoja.left + hoja.width * 0.56f, hoja.top + hoja.height * 0.82f)
            drawRect(c(CREMA), foto.topLeft, foto.size)
            val img = foto.deflate(4 * u)
            drawRect(c(mezcla(CIELO, ARCE, 0.4f)), img.topLeft, Size(img.width, img.height * 0.6f))
            drawRect(c(mezcla(MAR, NOGAL, 0.3f)), Offset(img.left, img.top + img.height * 0.6f), Size(img.width, img.height * 0.4f))
            val casco = Path().apply {
                moveTo(img.left + img.width * 0.12f, img.top + img.height * 0.56f); lineTo(img.right - img.width * 0.08f, img.top + img.height * 0.56f)
                lineTo(img.right - img.width * 0.18f, img.top + img.height * 0.7f); lineTo(img.left + img.width * 0.2f, img.top + img.height * 0.7f); close()
            }
            drawPath(casco, c(LACA))
            drawRect(c(CREMA), Offset(img.left + img.width * 0.26f, img.top + img.height * 0.46f), Size(img.width * 0.5f, img.height * 0.1f))
            listOf(0.34f, 0.5f, 0.66f).forEach { f -> drawRect(c(LACA), Offset(img.left + img.width * f, img.top + img.height * 0.28f), Size(img.width * 0.08f, img.height * 0.18f)) }
            listOf(foto.topLeft to Offset(1f, 1f), Offset(foto.right, foto.top) to Offset(-1f, 1f), Offset(foto.left, foto.bottom) to Offset(1f, -1f), foto.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x - s.x * 2 * u, o.y - s.y * 2 * u); lineTo(o.x + s.x * 9 * u, o.y - s.y * 2 * u); lineTo(o.x - s.x * 2 * u, o.y + s.y * 9 * u); close() }, c(CROMO_OSC))
            }
            val talon = Rect(hoja.left + hoja.width * 0.64f, hoja.top + hoja.height * 0.3f, hoja.left + hoja.width * 0.9f, hoja.top + hoja.height * 0.6f)
            drawRect(c(CREMA), talon.topLeft, talon.size)
            var py = talon.top + 2 * u
            while (py < talon.bottom) { drawCircle(c(LACA), 0.8f * u, Offset(talon.left, py)); py += 3.5f * u }
            drawLine(c(ORO), Offset(talon.left + 5 * u, talon.center.y), Offset(talon.right - 5 * u, talon.center.y), 1 * u)
        }
        // La tapa del álbum, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(tapa, cu.topLeft, cu.size, CornerRadius(2 * u))
            // El brillo de la laca, en dos franjas.
            drawRect(Color.White.copy(alpha = 0.06f), Offset(cu.left + cu.width * 0.58f, cu.top), Size(cu.width * 0.05f, cu.height))
            drawRect(Color.White.copy(alpha = 0.04f), Offset(cu.left + cu.width * 0.66f, cu.top), Size(cu.width * 0.015f, cu.height))
            // Las bandas de cromo de arriba y de abajo.
            cromo(Rect(bisagra, h * 0.11f, cu.right, h * 0.135f), oscuro, e.noche)
            cromo(Rect(bisagra, h * 0.85f, cu.right, h * 0.875f), oscuro, e.noche)
            // El panel de la izquierda: de día, el sol naciente de marquetería; de noche, el mar con la luna, como por el ojo de buey.
            val pn = Rect(w * 0.115f, h * 0.17f, w * 0.315f, h * 0.745f)
            clipRect(pn.left, pn.top, pn.right, pn.bottom) {
                if (e.noche) {
                    val horizonte = pn.top + pn.height * 0.62f
                    drawRect(c(CIELO_NOCHE), pn.topLeft, Size(pn.width, horizonte - pn.top))
                    drawRect(c(MAR_NOCHE), Offset(pn.left, horizonte), Size(pn.width, pn.bottom - horizonte))
                    val cl = Offset(pn.left + pn.width * 0.6f, pn.top + pn.height * 0.36f)
                    val rl = pn.width * 0.17f
                    drawCircle(LUNA, rl, cl)
                    drawCircle(c(CIELO_NOCHE), rl * 0.84f, Offset(cl.x + rl * 0.45f, cl.y - rl * 0.25f))
                    for (k in 0 until 5) {
                        val y = horizonte + 3 * u + k * 4 * u
                        val l = pn.width * (0.3f - 0.05f * k)
                        drawLine(LUNA.copy(alpha = 0.55f - 0.08f * k), Offset(cl.x - l / 2, y), Offset(cl.x + l / 2, y), 0.9f * u)
                    }
                    listOf(0.2f to 0.22f, 0.32f to 0.5f, 0.82f to 0.18f, 0.12f to 0.42f).forEach { (fx, fy) -> drawCircle(LUNA.copy(alpha = 0.8f), 0.8f * u, Offset(pn.left + pn.width * fx, pn.top + pn.height * fy)) }
                    drawLine(c(CREMA).copy(alpha = 0.3f), Offset(pn.left, horizonte), Offset(pn.right, horizonte), 0.6f * u)
                } else {
                    val sc = Offset(pn.center.x, pn.bottom)
                    val rr = pn.height * 1.2f
                    val n = 13
                    for (k in 0 until n) {
                        drawArc(dorado.copy(alpha = if (k % 2 == 0) 0.85f else 0.3f), 180f + k * 180f / n, 180f / n, true, Offset(sc.x - rr, sc.y - rr), Size(rr * 2, rr * 2))
                    }
                    val rs = pn.width * 0.32f
                    drawArc(dorado, 180f, 180f, true, Offset(sc.x - rs, sc.y - rs), Size(rs * 2, rs * 2))
                    listOf(0.78f, 0.56f).forEach { f -> drawArc(tapa, 180f, 180f, false, Offset(sc.x - rs * f, sc.y - rs * f), Size(rs * 2 * f, rs * 2 * f), style = Stroke(0.9f * u)) }
                }
            }
            drawRect(dorado, pn.topLeft, pn.size, style = Stroke(1 * u))
            drawRect(dorado.copy(alpha = 0.5f), pn.inflate(2.5f * u).topLeft, pn.inflate(2.5f * u).size, style = Stroke(0.5f * u))
            // La placa de laca con «PASAJE», arriba del panel.
            val pj = Rect(pn.left + 3 * u, pn.top + 3 * u, pn.right - 3 * u, pn.top + h * 0.095f)
            drawPath(escalonado(pj, pj.height * 0.3f), c(LACA))
            drawPath(escalonado(pj, pj.height * 0.3f), c(ORO), style = Stroke(0.6f * u))
            textoEn(medidor, "PASAJE", pj.deflate(2 * u), LetraExtra.poiret, c(ORO), 10f, espaciado = 2f, minimo = 6f)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(pn.left - 2 * u, h * 0.765f, pn.right + 2 * u, h * 0.835f), LetraExtra.poiret, rotulo, 9.5f, minimo = 6f)
            }
            // La línea de la naviera, sobre la cartela.
            textoEn(medidor, "LÍNEA TRANSATLÁNTICA · 1ª CLASE", Rect(w * 0.33f, h * 0.16f, w * 0.91f, h * 0.255f), LetraExtra.poiret, rotulo, 10.5f, espaciado = 0.3f, minimo = 5f)
            // La cartela escalonada de crema, con su filete doble, donde va el extracto.
            val ct = Rect(w * 0.34f, h * 0.275f, w * 0.9f, h * 0.74f)
            drawPath(escalonado(Rect(ct.left + 1.2f * u, ct.top + 1.8f * u, ct.right + 1.2f * u, ct.bottom + 1.8f * u), 10 * u), Color.Black.copy(alpha = 0.3f))
            drawPath(escalonado(ct, 10 * u), cremaTapa(oscuro, e.noche))
            drawPath(escalonado(ct, 10 * u), dorado, style = Stroke(1.2f * u))
            drawPath(escalonado(ct.deflate(3.5f * u), 7 * u), dorado.copy(alpha = 0.55f), style = Stroke(0.6f * u))
            // La fecha del pasaje, en uno o dos renglones.
            val (f1, f2) = partirFecha(e.fecha, 34)
            if (f2 == null) {
                textoEn(medidor, f1, Rect(w * 0.34f, h * 0.76f, w * 0.9f, h * 0.835f), LetraExtra.poiret, rotulo, 10.5f, minimo = 6f)
            } else {
                textoEn(medidor, f1, Rect(w * 0.34f, h * 0.752f, w * 0.9f, h * 0.802f), LetraExtra.poiret, rotulo, 9.5f, minimo = 6f)
                textoEn(medidor, f2, Rect(w * 0.34f, h * 0.802f, w * 0.9f, h * 0.845f), LetraExtra.poiret, rotulo.copy(alpha = 0.85f), 8.5f, minimo = 6f)
            }
            // Los chevrones dorados del margen: más cuanto más escribiste.
            val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
            val chevrones = 1 + (lleno * 5).toInt()
            for (j in 0 until chevrones) {
                val yy = h * (0.3f + 0.075f * j)
                drawPath(Path().apply { moveTo(w * 0.913f, yy + h * 0.03f); lineTo(w * 0.934f, yy); lineTo(w * 0.955f, yy + h * 0.03f) }, dorado, style = Stroke(1.1f * u))
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(2 * u))
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo geométrico: gradas doradas y rombos entre las bandas de cromo.
        val lomo = Rect(cu.left, cu.top, bisagra, cu.bottom)
        drawRect(tapa, lomo.topLeft, lomo.size)
        drawRect(Color.Black.copy(alpha = 0.28f), lomo.topLeft, lomo.size)
        cromo(Rect(lomo.left, h * 0.11f, lomo.right, h * 0.135f), oscuro, e.noche)
        cromo(Rect(lomo.left, h * 0.85f, lomo.right, h * 0.875f), oscuro, e.noche)
        var gy = h * 0.17f
        while (gy < h * 0.8f) {
            val m = lomo.center.x
            drawPath(Path().apply { moveTo(m, gy); lineTo(m + lomo.width * 0.3f, gy + h * 0.04f); lineTo(m, gy + h * 0.08f); lineTo(m - lomo.width * 0.3f, gy + h * 0.04f); close() }, dorado, style = Stroke(0.7f * u))
            drawLine(dorado.copy(alpha = 0.6f), Offset(lomo.left + 2 * u, gy + h * 0.1f), Offset(lomo.right - 2 * u, gy + h * 0.1f), 0.6f * u)
            gy += h * 0.12f
        }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 1 * u)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 126.dp

    /**
     * La cabecera del pasaje: arriba al centro, el ojo de buey de cromo con sus pernos (de día, el cielo y el mar con el sol
     * en el horizonte; de noche, la luna y su reflejo), con los rayos del sol déco abiertos a los lados y tres filetes
     * escalonados; debajo, «PASAJE Nº … · LÍNEA TRANSATLÁNTICA» espaciado, la cartela de esquinas escalonadas con chevrones
     * de jade donde va la fecha, el detalle y, al pie, los tres filetes escalonados.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val cx = w / 2
        val ojo = Offset(cx, 17 * u); val ro = 12.5f * u
        // Los rayos del sol déco, abiertos hacia arriba a los dos lados del ojo de buey.
        for (k in 0..8) {
            listOf(188.0 + k * 9.0, 352.0 - k * 9.0).forEach { g ->
                val a = Math.toRadians(g)
                val ini = ro + 6 * u
                drawLine(p.acento.copy(alpha = if (k % 2 == 0) 0.35f else 0.2f), Offset(ojo.x + ini * cos(a).toFloat(), ojo.y + ini * sin(a).toFloat()),
                    Offset(ojo.x + w * 0.5f * cos(a).toFloat(), ojo.y + w * 0.5f * sin(a).toFloat()), 0.8f * u)
            }
        }
        // Los tres filetes escalonados a cada lado, a la altura del ojo.
        for (k in 0..2) {
            val y = ojo.y + 3 * u + k * 3.5f * u
            val largo = w * (0.3f - k * 0.07f)
            drawLine(p.acento.copy(alpha = 0.6f), Offset(cx - ro - 6 * u - largo, y), Offset(cx - ro - 6 * u, y), 0.8f * u)
            drawLine(p.acento.copy(alpha = 0.6f), Offset(cx + ro + 6 * u, y), Offset(cx + ro + 6 * u + largo, y), 0.8f * u)
        }
        // El ojo de buey.
        drawCircle(c(CROMO_OSC), ro + 3 * u, ojo)
        drawCircle(c(CROMO), ro + 2 * u, ojo)
        drawCircle(c(CROMO_LUZ).copy(alpha = 0.7f), ro + 2.6f * u, ojo, style = Stroke(0.6f * u))
        for (k in 0 until 8) {
            val a = Math.toRadians(22.5 + 45.0 * k)
            drawCircle(c(CROMO_OSC), 0.8f * u, Offset(ojo.x + (ro + 1.3f * u) * cos(a).toFloat(), ojo.y + (ro + 1.3f * u) * sin(a).toFloat()))
        }
        clipPath(Path().apply { addOval(Rect(ojo, ro)) }) {
            val horizonte = ojo.y + 2 * u
            drawRect(c(if (e.noche) CIELO_NOCHE else CIELO), Offset(ojo.x - ro, ojo.y - ro), Size(2 * ro, horizonte - ojo.y + ro))
            drawRect(c(if (e.noche) MAR_NOCHE else MAR), Offset(ojo.x - ro, horizonte), Size(2 * ro, ro))
            if (e.noche) {
                drawCircle(LUNA, 3.6f * u, Offset(ojo.x + 4 * u, ojo.y - 5 * u))
                for (k in 0 until 3) drawLine(LUNA.copy(alpha = 0.6f - k * 0.15f), Offset(ojo.x + (2.5f - k) * u, horizonte + (2 + k * 2.5f) * u), Offset(ojo.x + (5.5f + k) * u, horizonte + (2 + k * 2.5f) * u), 0.8f * u)
                listOf(Offset(-6f, -6f), Offset(-2f, -9f), Offset(9f, -2f)).forEach { k -> drawCircle(LUNA.copy(alpha = 0.8f), 0.5f * u, Offset(ojo.x + k.x * u, ojo.y + k.y * u)) }
            } else {
                drawArc(c(ORO), 180f, 180f, true, Offset(ojo.x - 5 * u, horizonte - 5 * u), Size(10 * u, 10 * u))
                for (k in 0 until 3) drawLine(Color.White.copy(alpha = 0.35f), Offset(ojo.x - (8 - k * 2) * u, horizonte + (3 + k * 2.6f) * u), Offset(ojo.x + (2 + k * 3) * u, horizonte + (3 + k * 2.6f) * u), 0.6f * u)
            }
            drawLine(Color.White.copy(alpha = 0.3f), Offset(ojo.x - ro * 0.6f, ojo.y - ro * 0.75f), Offset(ojo.x - ro * 0.95f, ojo.y - ro * 0.2f), 1.2f * u)
        }
        textoEn(medidor, e.rotulo, Rect(4 * u, 37 * u, w - 4 * u, 51 * u), LetraExtra.poiret, p.acento, 11f, espaciado = 1.6f, minimo = 6.5f)
        // La cartela escalonada de la fecha, con sus chevrones de jade.
        val cart = Rect(3 * u, 55 * u, w - 3 * u, 101 * u)
        drawPath(escalonado(cart, 10 * u), p.acento.copy(alpha = 0.6f), style = Stroke(1.1f * u))
        drawPath(escalonado(cart.deflate(3 * u), 8 * u), p.acento.copy(alpha = 0.28f), style = Stroke(0.6f * u))
        listOf(cart.left + 10 * u to 1f, cart.right - 10 * u to -1f).forEach { (x0, d) ->
            for (k in 0 until 3) {
                val x = x0 + d * k * 3.5f * u
                drawPath(Path().apply { moveTo(x, cart.center.y - 4.5f * u); lineTo(x + d * 3 * u, cart.center.y); lineTo(x, cart.center.y + 4.5f * u) },
                    p.detalle.copy(alpha = 0.75f - k * 0.2f), style = Stroke(1 * u))
            }
        }
        fechaCon(medidor, e.fecha, Rect(cart.left + 24 * u, cart.top + 3 * u, cart.right - 24 * u, cart.bottom - 3 * u), LetraExtra.pasaje, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(6 * u, 104 * u, w - 6 * u, 117 * u), LetraExtra.pasaje, p.tenue, 10.5f, minimo = 6.5f)
        listOf(0.62f, 0.42f, 0.22f).forEachIndexed { k, f ->
            drawLine(p.acento.copy(alpha = 0.6f), Offset(cx - w * f / 2, (119.5f + k * 2.6f) * u), Offset(cx + w * f / 2, (119.5f + k * 2.6f) * u), 0.8f * u)
        }
    }

    /**
     * El papel de cartas del salón de escribir: el sol de rayos que baja desde lo alto, los dobles filetes de los márgenes con
     * sus remates escalonados, las escuadras escalonadas de las esquinas y, al pie, los tres filetes y una fila de chevrones de
     * jade. En modo oscuro, la luz del aplique de vidrio esmerilado del salón, en abanico desde arriba.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val o = Offset(w / 2, -h * 0.04f)
        for (i in 0..22) {
            val a = PI * (0.1 + 0.8 * i / 22.0)
            drawLine(p.acento.copy(alpha = if (i % 2 == 0) 0.055f else 0.03f), o, Offset(o.x + h * 0.62f * cos(a).toFloat(), o.y + h * 0.62f * sin(a).toFloat()), 1.2f * u)
        }
        // Los dobles filetes de los márgenes, con sus remates escalonados.
        val y0 = 70 * u; val y1 = h - 70 * u
        listOf(12 * u to 1f, w - 12 * u to -1f).forEach { (x, d) ->
            drawLine(p.acento.copy(alpha = 0.25f), Offset(x, y0), Offset(x, y1), 0.8f * u)
            drawLine(p.acento.copy(alpha = 0.18f), Offset(x + d * 3.5f * u, y0 + 8 * u), Offset(x + d * 3.5f * u, y1 - 8 * u), 0.6f * u)
            listOf(y0 to 1f, y1 to -1f).forEach { (y, s) ->
                for (k in 0..2) drawLine(p.acento.copy(alpha = 0.3f), Offset(x, y + s * k * 3 * u), Offset(x + d * (3 + k * 2.5f) * u, y + s * k * 3 * u), 0.7f * u)
            }
        }
        // Las escuadras escalonadas de las esquinas.
        val m = 7 * u
        listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (c0, d) ->
            val esq = Path().apply {
                moveTo(c0.x + d.x * 30 * u, c0.y); lineTo(c0.x, c0.y); lineTo(c0.x, c0.y + d.y * 30 * u)
                moveTo(c0.x + d.x * 20 * u, c0.y + d.y * 4 * u); lineTo(c0.x + d.x * 4 * u, c0.y + d.y * 4 * u); lineTo(c0.x + d.x * 4 * u, c0.y + d.y * 20 * u)
                moveTo(c0.x + d.x * 10 * u, c0.y + d.y * 8 * u); lineTo(c0.x + d.x * 8 * u, c0.y + d.y * 8 * u); lineTo(c0.x + d.x * 8 * u, c0.y + d.y * 10 * u)
            }
            drawPath(esq, p.acento.copy(alpha = 0.45f), style = Stroke(0.9f * u))
        }
        listOf(0.7f, 0.5f, 0.3f).forEachIndexed { k, f ->
            drawLine(p.acento.copy(alpha = 0.22f), Offset(w * (1 - f) / 2, h - (16 + k * 5) * u), Offset(w * (1 + f) / 2, h - (16 + k * 5) * u), 1 * u)
        }
        for (k in -3..3) {
            val x = w / 2 + k * 9 * u
            drawPath(Path().apply { moveTo(x - 3 * u, h - 36 * u); lineTo(x, h - 32 * u); lineTo(x + 3 * u, h - 36 * u) }, p.detalle.copy(alpha = 0.3f), style = Stroke(0.9f * u))
        }
        if (oscuro && !p.oscura) {
            val aplique = Offset(w * 0.5f, -h * 0.01f)
            val luz = Color(0xFFF2D27A)
            penumbraCon(aplique, luz, 0.16f)
            for (k in 0 until 5) hazCon(aplique, 50f + k * 20f, 8f, h * 0.85f, luz.copy(alpha = 0.03f))
        }
    }

    /** El tubo de cromo al pie de la barra, con un filete de jade y un escalón de oro al centro. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        val r = Rect(0f, h - 6.5f * u, w, h - 3 * u)
        drawRect(Brush.verticalGradient(listOf(c(CROMO_LUZ), c(CROMO), c(CROMO_OSC)), r.top, r.bottom), r.topLeft, r.size)
        drawLine(c(Color(0xFF1F6B57)), Offset(0f, h - 1.5f * u), Offset(w, h - 1.5f * u), 1 * u)
        val cx = w / 2
        drawPath(Path().apply {
            moveTo(cx - 15 * u, r.top); lineTo(cx - 15 * u, r.top - 1.8f * u); lineTo(cx - 9 * u, r.top - 1.8f * u); lineTo(cx - 9 * u, r.top - 3.6f * u)
            lineTo(cx - 3 * u, r.top - 3.6f * u); lineTo(cx - 3 * u, r.top - 5.4f * u); lineTo(cx + 3 * u, r.top - 5.4f * u); lineTo(cx + 3 * u, r.top - 3.6f * u)
            lineTo(cx + 9 * u, r.top - 3.6f * u); lineTo(cx + 9 * u, r.top - 1.8f * u); lineTo(cx + 15 * u, r.top - 1.8f * u); lineTo(cx + 15 * u, r.top); close()
        }, c(ORO))
    }

    /** La placa de laca negra con las esquinas escalonadas, el filete de oro y un chevrón en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        translate(1 * u, 1.8f * u) { drawPath(escalonado(r, 7 * u), Color.Black.copy(alpha = 0.25f)) }
        drawPath(escalonado(r, 7 * u), Brush.verticalGradient(listOf(mezcla(LACA, Color.White, 0.12f), LACA, LACA)))
        drawPath(escalonado(r.deflate(3 * u), 5 * u), c(ORO), style = Stroke(0.9f * u))
        listOf(7.5f * u to 1f, w - 7.5f * u to -1f).forEach { (x, d) ->
            drawPath(Path().apply { moveTo(x, h / 2 - 3.5f * u); lineTo(x + d * 2.5f * u, h / 2); lineTo(x, h / 2 + 3.5f * u) }, c(ORO), style = Stroke(1 * u))
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFE9D08C)

    // ---------------------------------------------------------------- la libreta de momentos

    /** El nácar del abanico, el jade del cierre, la seda de las cintas y el vidrio esmerilado de la lámpara. */
    private val NACAR = Color(0xFFEDE6DA)
    private val JADE = Color(0xFF1F6B57)
    private val CINTAS = listOf(Color(0xFFB5523B), Color(0xFF1F6B57), Color(0xFFC9A54A), Color(0xFF7A2638))
    private val ESMERILADO = Color(0xFFF3E7C8)
    private val LUZ_LAMPARA = Color(0xFFFFDDA0)

    override val altoLibreta: Dp = 256.dp

    /** Un abanico de rayos de oro y nácar alternos sobre la base [b], de radio [r], con su medio sol de jade y su aro. */
    private fun DrawScope.abanicoDeco(b: Offset, r: Float, n: Int, oro: Color, nacar: Color, jade: Color) {
        val u = density
        for (i in 0 until n) drawArc(if (i % 2 == 0) oro else nacar, 180f + i * 180f / n, 180f / n, true, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r))
        drawArc(jade, 180f, 180f, true, Offset(b.x - r * 0.32f, b.y - r * 0.32f), Size(r * 0.64f, r * 0.64f))
        drawArc(oro, 180f, 180f, false, Offset(b.x - r * 1.1f, b.y - r * 1.1f), Size(r * 2.2f, r * 2.2f), style = Stroke(1.2f * u))
    }

    /**
     * La libreta de laca negra sobre el escritorio de arce con su canto de ébano y su fleje de cromo, contra la pared verde
     * petróleo con sus pilastras de filetes dorados. A la izquierda, la lámpara de abanico: la pantalla de vidrio esmerilado en
     * medio sol con sus nervios de cromo, sobre su pie escalonado; a la derecha, el reloj de mesa escalonado de cromo y la
     * estilográfica. La libreta lleva incrustado el abanico de oro y nácar con su medio sol de jade, el marco de filetes con
     * las esquinas escalonadas y la cartela con el nombre en oro y la cuenta; engorda con las hojas y por abajo cuelgan las
     * cintas de seda de los programas guardados (una cada 30 hojas, hasta 6). Se abre como un díptico: el cierre de jade del
     * centro se corre y las dos hojas de la tapa se apartan hacia los lados, cada una con su mitad del abanico; dentro, el
     * programa de a bordo en papel marfil. En modo oscuro, el salón de noche: solo alumbra la lámpara de abanico (el único
     * degradado de la escena).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto, 0.26f)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF09090B), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF09090B), 0.3f) else x
        // La pared con sus pilastras de filetes dorados.
        val mesaY = h * 0.5f
        drawRect(c(PARED), Offset.Zero, Size(w, mesaY))
        listOf(0.04f, 0.5f, 0.96f).forEach { f ->
            val px = w * f
            drawRect(c(mezcla(PARED, Color.Black, 0.18f)), Offset(px - 14 * u, 0f), Size(28 * u, mesaY))
            listOf(-9f, 0f, 9f).forEach { dx -> drawLine(c(ORO).copy(alpha = 0.55f), Offset(px + dx * u, 0f), Offset(px + dx * u, mesaY), 0.8f * u) }
            for (k in 0 until 3) drawRect(c(ORO).copy(alpha = 0.55f), Offset(px - (14 - k * 4) * u, mesaY * 0.2f + k * 4 * u), Size((28 - k * 8) * u, 1.2f * u))
        }
        // El escritorio de arce chapeado, con su canto de ébano y el fleje de cromo.
        drawRect(c(ARCE), Offset(0f, mesaY), Size(w, h - mesaY))
        vetas(Rect(0f, mesaY, w, h), c(ARCE_VETA).copy(alpha = 0.6f), 95, densidad = 0.9f)
        drawRect(c(LACA), Offset(0f, mesaY), Size(w, 4 * u))
        drawRect(c(CROMO), Offset(0f, mesaY + 4 * u), Size(w, 1.6f * u))
        drawRect(c(LACA), Offset(0f, h - 10 * u), Size(w, 10 * u))
        drawRect(c(CROMO_LUZ), Offset(0f, h - 10 * u), Size(w, 1.4f * u))
        // La lámpara de abanico, a la izquierda.
        val lb = Offset(w * 0.1f, h * 0.36f)
        listOf(26f, 18f, 10f).forEachIndexed { k, a0 -> drawRect(c(if (k == 1) CROMO_OSC else CROMO), Offset(lb.x - a0 * u / 2 * 1.4f, h * 0.6f - (k + 1) * 4 * u), Size(a0 * 1.4f * u, 4 * u)) }
        drawRect(c(CROMO), Offset(lb.x - 1.6f * u, lb.y), Size(3.2f * u, h * 0.6f - 12 * u - lb.y))
        val rl = 30 * u
        drawArc(if (oscuro) LUZ_LAMPARA else c(ESMERILADO), 180f, 180f, true, Offset(lb.x - rl, lb.y - rl), Size(2 * rl, 2 * rl))
        for (k in 1 until 7) {
            val ang = Math.toRadians(180.0 + k * 180.0 / 7)
            drawLine(c(CROMO_OSC), lb, Offset(lb.x + rl * cos(ang).toFloat(), lb.y + rl * sin(ang).toFloat()), 1 * u)
        }
        drawArc(c(CROMO), 180f, 180f, false, Offset(lb.x - rl, lb.y - rl), Size(2 * rl, 2 * rl), style = Stroke(2 * u))
        drawRect(c(CROMO), Offset(lb.x - rl, lb.y - 1 * u), Size(2 * rl, 3 * u))
        // A la derecha, el reloj de mesa escalonado y la estilográfica.
        val rj = Rect(w * 0.82f, h * 0.3f, w * 0.97f, h * 0.58f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(rj.left + 3 * u, rj.bottom), Size(rj.width, 4 * u))
        drawPath(Path().apply {
            moveTo(rj.left, rj.bottom); lineTo(rj.left, rj.top + 16 * u); lineTo(rj.left + 6 * u, rj.top + 16 * u); lineTo(rj.left + 6 * u, rj.top + 8 * u)
            lineTo(rj.left + 12 * u, rj.top + 8 * u); lineTo(rj.left + 12 * u, rj.top); lineTo(rj.right - 12 * u, rj.top); lineTo(rj.right - 12 * u, rj.top + 8 * u)
            lineTo(rj.right - 6 * u, rj.top + 8 * u); lineTo(rj.right - 6 * u, rj.top + 16 * u); lineTo(rj.right, rj.top + 16 * u); lineTo(rj.right, rj.bottom); close()
        }, c(LACA))
        drawRect(c(CROMO), Offset(rj.left, rj.bottom - 4 * u), Size(rj.width, 4 * u))
        val esf = Offset(rj.center.x, rj.top + rj.height * 0.55f); val re = rj.width * 0.32f
        drawCircle(c(CROMO), re + 1.6f * u, esf); drawCircle(cp(CREMA), re, esf)
        for (k in 0 until 12) { val ang = Math.toRadians(k * 30.0); drawLine(c(LACA), Offset(esf.x + re * 0.78f * cos(ang).toFloat(), esf.y + re * 0.78f * sin(ang).toFloat()),
            Offset(esf.x + re * 0.92f * cos(ang).toFloat(), esf.y + re * 0.92f * sin(ang).toFloat()), (if (k % 3 == 0) 1.2f else 0.6f) * u) }
        drawLine(c(LACA), esf, esf + Offset(re * 0.45f, -re * 0.25f), 1.2f * u); drawLine(c(LACA), esf, esf + Offset(-re * 0.1f, -re * 0.65f), 0.9f * u)
        rotate(-16f, Offset(w * 0.88f, h * 0.84f)) {
            drawRoundRect(c(LACA), Offset(w * 0.88f - 26 * u, h * 0.84f - 3 * u), Size(42 * u, 6 * u), CornerRadius(3 * u))
            drawRect(c(ORO), Offset(w * 0.88f - 4 * u, h * 0.84f - 3 * u), Size(2 * u, 6 * u))
            drawRect(c(CROMO), Offset(w * 0.88f + 6 * u, h * 0.84f - 3.5f * u), Size(14 * u, 1.4f * u))
            drawPath(Path().apply { moveTo(w * 0.88f + 16 * u, h * 0.84f - 3 * u); lineTo(w * 0.88f + 24 * u, h * 0.84f); lineTo(w * 0.88f + 16 * u, h * 0.84f + 3 * u); close() }, c(ORO))
        }
        // La luz de la lámpara.
        if (oscuro) drawRect(Brush.radialGradient(listOf(LUZ_LAMPARA.copy(alpha = 0.34f), LUZ_LAMPARA.copy(alpha = 0.12f), Color.Transparent), Offset(lb.x, lb.y - 6 * u), w * 0.85f))
        // La libreta de laca.
        val ancho = w * 0.56f; val alto = minOf(ancho * 0.66f, h * 0.52f)
        val r = Rect(w * 0.22f, h * 0.34f, w * 0.22f + ancho, h * 0.34f + alto)
        val g = (2.5f + 12f * lleno) * u
        val mitad = r.center.x
        // Las cintas de seda de los programas guardados, que cuelgan por abajo.
        val n = (1 + e.hojas / 30).coerceAtMost(6)
        for (k in 0 until n) {
            val cx = r.left + ancho * (0.18f + 0.12f * k) + 4 * u * azar(e.semilla, 130 + k)
            val largo = (14f + 12f * azar(e.semilla, 140 + k)) * u
            val cinta = Path().apply { moveTo(cx, r.bottom); lineTo(cx + 5 * u, r.bottom); lineTo(cx + 5 * u + 2 * u, r.bottom + g + largo); lineTo(cx + 4.5f * u, r.bottom + g + largo - 3 * u)
                lineTo(cx + 2 * u, r.bottom + g + largo); close() }
            drawPath(cinta, c(CINTAS[k % CINTAS.size]))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), r.topLeft + Offset(5 * u, 5 * u + g), r.size)
        drawRect(c(LACA), r.topLeft + Offset(0f, g), r.size)
        cantoLibreta(r, g * 0.85f, c(CREMA), c(ORO).copy(alpha = 0.5f))
        // Por dentro: el programa de a bordo en papel marfil, con el barco en su cartela escalonada.
        if (abre > 0.01f) {
            drawRect(cp(CREMA), r.topLeft, r.size)
            drawRect(c(ORO), r.deflate(6 * u).topLeft, r.deflate(6 * u).size, style = Stroke(0.8f * u))
            textoEn(medidor, "PROGRAMA", Rect(r.left + 20 * u, r.top + 12 * u, r.right - 20 * u, r.top + 28 * u), letra, c(LACA), 11f, peso = FontWeight.Bold, espaciado = 3f)
            val bc = Offset(r.center.x, r.top + alto * 0.5f)
            drawPath(Path().apply { moveTo(bc.x - 40 * u, bc.y); lineTo(bc.x + 44 * u, bc.y); lineTo(bc.x + 36 * u, bc.y + 10 * u); lineTo(bc.x - 34 * u, bc.y + 10 * u); close() }, c(LACA))
            drawRect(c(CREMA), Offset(bc.x - 30 * u, bc.y - 8 * u), Size(56 * u, 8 * u)); drawRect(c(LACA), Offset(bc.x - 30 * u, bc.y - 8 * u), Size(56 * u, 8 * u), style = Stroke(0.8f * u))
            listOf(-14f, 0f, 14f).forEach { dx -> drawRect(c(ROJO_DECO), Offset(bc.x + dx * u - 3 * u, bc.y - 20 * u), Size(6 * u, 12 * u)); drawRect(c(LACA), Offset(bc.x + dx * u - 3 * u, bc.y - 20 * u), Size(6 * u, 3 * u)) }
            for (k in 0 until 3) drawLine(c(LACA).copy(alpha = 0.5f), Offset(r.left + 30 * u, r.bottom - (26 - k * 7) * u), Offset(r.right - (if (k == 1) 70 else 30) * u, r.bottom - (26 - k * 7) * u), 0.8f * u)
        }
        // Las dos hojas de la tapa: cada una dibuja la tapa entera recortada a su mitad y se aparta hacia su lado.
        val queda = 1f - abre
        listOf(true, false).forEach { izq ->
            val borde = if (izq) r.left else r.right
            if (queda > 0.02f) scale(queda, 1f, Offset(borde, r.center.y)) {
                clipRect(if (izq) r.left else mitad, r.top, if (izq) mitad else r.right, r.bottom) {
                    drawRect(c(LACA), r.topLeft, r.size)
                    drawPath(Path().apply { moveTo(r.left + ancho * 0.55f, r.top); lineTo(r.left + ancho * 0.72f, r.top); lineTo(r.left + ancho * 0.36f, r.bottom); lineTo(r.left + ancho * 0.19f, r.bottom); close() },
                        Color.White.copy(alpha = if (oscuro) 0.07f else 0.05f))
                    val m = 7 * u
                    drawRect(cp(ORO), r.deflate(m).topLeft, r.deflate(m).size, style = Stroke(1.3f * u))
                    drawRect(cp(ORO).copy(alpha = 0.5f), r.deflate(m * 1.6f).topLeft, r.deflate(m * 1.6f).size, style = Stroke(0.6f * u))
                    listOf(Offset(r.left + m, r.top + m) to Offset(1f, 1f), Offset(r.right - m, r.top + m) to Offset(-1f, 1f),
                        Offset(r.left + m, r.bottom - m) to Offset(1f, -1f), Offset(r.right - m, r.bottom - m) to Offset(-1f, -1f)).forEach { (o, s) ->
                        for (k in 1..3) drawRect(cp(ORO), Offset(if (s.x > 0) o.x else o.x - k * 4 * u, if (s.y > 0) o.y else o.y - (4 - k) * 4 * u), Size(k * 4 * u, (4 - k) * 4 * u))
                    }
                    // El abanico incrustado.
                    val b = Offset(mitad, r.top + alto * 0.5f)
                    abanicoDeco(b, alto * 0.32f, 13, cp(ORO), cp(NACAR).copy(alpha = 0.9f), cp(JADE))
                    listOf(1.1f, 0.8f, 0.5f).forEachIndexed { k, f -> drawRect(cp(ORO), Offset(b.x - alto * 0.32f * f, b.y + 2 * u + k * 4.5f * u), Size(2 * alto * 0.32f * f, 3 * u)) }
                    // La cartela con el nombre en oro y la cuenta.
                    val ca = Rect(r.left + ancho * 0.14f, r.top + alto * 0.66f, r.right - ancho * 0.14f, r.bottom - alto * 0.08f)
                    drawPath(Path().apply {
                        val s = 5 * u
                        moveTo(ca.left + s, ca.top); lineTo(ca.right - s, ca.top); lineTo(ca.right - s, ca.top + s); lineTo(ca.right, ca.top + s); lineTo(ca.right, ca.bottom - s)
                        lineTo(ca.right - s, ca.bottom - s); lineTo(ca.right - s, ca.bottom); lineTo(ca.left + s, ca.bottom); lineTo(ca.left + s, ca.bottom - s); lineTo(ca.left, ca.bottom - s)
                        lineTo(ca.left, ca.top + s); lineTo(ca.left + s, ca.top + s); close()
                    }, cp(ORO), style = Stroke(1 * u))
                    textoEn(medidor, e.nombre, Rect(ca.left + 8 * u, ca.top + 2 * u, ca.right - 8 * u, ca.top + ca.height * 0.58f), letra, ORO_CLARO.let { if (oscuro) mezcla(it, Color.Black, 0.15f) else it }, 13f, peso = FontWeight.Bold, espaciado = 1f, minimo = 7f)
                    textoEn(medidor, e.cuenta, Rect(ca.left + 8 * u, ca.top + ca.height * 0.56f, ca.right - 8 * u, ca.bottom - 2 * u), letra, cp(CREMA).copy(alpha = 0.85f), 9f, minimo = 6f)
                    if (abre > 0f) drawRect(Color.Black.copy(alpha = 0.4f * abre), r.topLeft, r.size)
                }
            }
        }
        // El cierre de jade del centro, con su pasador de oro: se corre hacia arriba y se va.
        val cierre = 1f - suelta
        if (cierre > 0f) {
            val cj = Rect(mitad - 6 * u, r.center.y - 9 * u - suelta * 20 * u, mitad + 6 * u, r.center.y + 9 * u - suelta * 20 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f * cierre), cj.topLeft + Offset(1 * u, 1.5f * u), cj.size, CornerRadius(2 * u))
            drawRoundRect(c(JADE).copy(alpha = cierre), cj.topLeft, cj.size, CornerRadius(2 * u))
            drawRoundRect(c(ORO).copy(alpha = cierre), cj.topLeft, cj.size, CornerRadius(2 * u), style = Stroke(1 * u))
            drawCircle(c(ORO).copy(alpha = cierre), 2.2f * u, cj.center)
        }
    }

    private val ORO_CLARO = Color(0xFFE6C77A)
    private val ROJO_DECO = Color(0xFFB5523B)

    /**
     * La pestaña del día es una ficha de laca negra con el remate escalonado arriba y el filete de cromo: la letra de la semana
     * en el escalón, el día grande en oro y, abajo, la franja de esmalte en el color del ánimo entre dos filetes de cromo (sin
     * ánimo, la franja queda en laca con un chevron dorado).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
        val s = 4 * u
        val forma = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top + 2 * s); lineTo(r.left + s, r.top + 2 * s); lineTo(r.left + s, r.top + s); lineTo(r.left + 2 * s, r.top + s)
            lineTo(r.left + 2 * s, r.top); lineTo(r.right - 2 * s, r.top); lineTo(r.right - 2 * s, r.top + s); lineTo(r.right - s, r.top + s); lineTo(r.right - s, r.top + 2 * s)
            lineTo(r.right, r.top + 2 * s); lineTo(r.right, r.bottom); close()
        }
        translate(1.2f * u, 1.6f * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, c(if ((e.semilla and 3) == 0) ALBUMES[1] else LACA))
        drawPath(forma, c(CROMO), style = Stroke(1 * u))
        textoEn(medidor, e.semana, Rect(r.left + 2 * s, r.top + 1 * u, r.right - 2 * s, r.top + 2 * s + 2 * u), letra, c(CROMO_LUZ), 7.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 2 * u, r.top + 12 * u, r.right - 2 * u, r.top + 33 * u), letra, c(ORO_CLARO), 19f, peso = FontWeight.Bold, minimo = 9f)
        val f = Rect(r.left + 1 * u, r.bottom - 14 * u, r.right - 1 * u, r.bottom - 5 * u)
        if (e.tono != null) drawRect(e.tono, f.topLeft, f.size)
        else drawPath(Path().apply { moveTo(f.center.x - 7 * u, f.top + 2 * u); lineTo(f.center.x, f.bottom - 2 * u); lineTo(f.center.x + 7 * u, f.top + 2 * u) }, c(ORO), style = Stroke(1.2f * u))
        drawLine(c(CROMO_LUZ), Offset(f.left, f.top), Offset(f.right, f.top), 1 * u)
        drawLine(c(CROMO_OSC), Offset(f.left, f.bottom), Offset(f.right, f.bottom), 1 * u)
    }

    /** El mes en oro sobre una banda de laca negra con sus remates escalonados de cromo y dos abanicos pequeños. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(10 * u, 6 * u, w - 10 * u, h - 6 * u)
        drawLine(c(CROMO), Offset(0f, h / 2), Offset(w, h / 2), 1.4f * u)
        drawRect(c(LACA), b.topLeft, b.size)
        for (k in 0 until 3) {
            val d = (k + 1) * 4 * u
            drawRect(c(if (k % 2 == 0) CROMO else CROMO_OSC), Offset(b.left - d + 4 * u, b.top + k * 3 * u), Size(4 * u, b.height - k * 6 * u))
            drawRect(c(if (k % 2 == 0) CROMO else CROMO_OSC), Offset(b.right + d - 8 * u, b.top + k * 3 * u), Size(4 * u, b.height - k * 6 * u))
        }
        drawLine(c(ORO), Offset(b.left + 6 * u, b.top + 2.5f * u), Offset(b.right - 6 * u, b.top + 2.5f * u), 0.7f * u)
        drawLine(c(ORO), Offset(b.left + 6 * u, b.bottom - 2.5f * u), Offset(b.right - 6 * u, b.bottom - 2.5f * u), 0.7f * u)
        val medida = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)).size.width
        val mitad = minOf(medida / 2f + 18 * u, b.width / 2 - 14 * u)
        listOf(-1f, 1f).forEach { s -> abanicoDeco(Offset(b.center.x + s * mitad, b.bottom - 4 * u), 9 * u, 7, c(ORO), c(NACAR), c(JADE)) }
        textoEn(medidor, texto, Rect(b.center.x - mitad + 12 * u, b.top + 3 * u, b.center.x + mitad - 12 * u, b.bottom - 3 * u), letra, c(ORO_CLARO), 13f, peso = FontWeight.Bold, espaciado = 2f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El esmalte de jade del medallón, más claro en su brillo. */
    private val JADE_LUZ = Color(0xFF4FA88C)

    /** La solapa escalonada de la carta: tres gradas por lado que bajan hasta una meseta en el centro, a [hondo] del borde. */
    private fun solapaGradas(l: Float, r: Float, top: Float, hondo: Float): Path = Path().apply {
        val w = r - l; val cx = (l + r) / 2
        moveTo(l, top); lineTo(r, top)
        val gx = listOf(0f, 0.12f, 0.24f, 0.36f); val gy = listOf(0.3f, 0.55f, 0.8f, 1f)
        for (i in 0 until 4) { lineTo(r - w * gx[i], top + hondo * gy[i]); if (i < 3) lineTo(r - w * gx[i + 1], top + hondo * gy[i]) }
        lineTo(cx + w * 0.06f, top + hondo); lineTo(cx - w * 0.06f, top + hondo)
        lineTo(l + w * gx[3], top + hondo)
        for (i in 3 downTo 1) { lineTo(l + w * gx[i], top + hondo * gy[i - 1]); lineTo(l + w * gx[i - 1], top + hondo * gy[i - 1]) }
        close()
    }

    /** El medallón de esmalte: un octógono de jade con su aro de oro y el abanico de oro y nácar. */
    private fun DrawScope.medallon(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val oct = Path().apply { for (i in 0 until 8) { val a = (PI / 8 + i * PI / 4).toFloat(); val p = Offset(c.x + cos(a) * r, c.y + sin(a) * r); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        translate(r * 0.06f, r * 0.1f) { drawPath(oct, Color.Black.copy(alpha = 0.4f * alfa)) }
        drawPath(oct, k(ORO))
        scale(0.84f, 0.84f, c) { drawPath(oct, k(JADE)) }
        scale(0.84f, 0.84f, c) { drawPath(oct, k(ORO_CLARO), style = Stroke(r * 0.05f)) }
        abanicoDeco(Offset(c.x, c.y + r * 0.32f), r * 0.55f, 7, k(ORO), k(NACAR), k(JADE_LUZ))
        drawLine(k(ORO), Offset(c.x - r * 0.55f, c.y + r * 0.34f), Offset(c.x + r * 0.55f, c.y + r * 0.34f), r * 0.06f)
        drawArc(k(Color.White.copy(alpha = 0.35f)), 205f, 55f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** Un botón de cierre: el disco de oro con su remache, donde se enrolla el cordón. */
    private fun DrawScope.botonCierre(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.4f), r, c + Offset(r * 0.15f, r * 0.2f))
        drawCircle(t(ORO, oscuro, false), r, c)
        drawCircle(t(ORO_CLARO, oscuro, false), r * 0.7f, c, style = Stroke(r * 0.12f))
        drawCircle(t(LACA, oscuro, false), r * 0.2f, c)
    }

    /**
     * El sobre de laca negra del salón de primera: papel negro satinado con el doble filete de oro y las esquinas en escalón,
     * la solapa escalonada como un rascacielos con su sol de rayos de oro y, dentro, el forro de oro con chevrones. La
     * cierra el medallón octogonal de esmalte de jade con el abanico de oro y nácar; las de al futuro, además, el cordón de
     * oro que da vueltas en ocho entre dos botones, uno en la solapa y otro en el sobre. A quién va y las fechas, en crema
     * sobre el negro. Al abrirlo, el medallón se parte, la solapa sube con sus gradas y sale la carta con su filete. En modo
     * oscuro, la laca casi se funde con la noche y solo el oro y la letra crema relucen.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.25f), Offset(l + 1.5f * u, top + 2.5f * u), b.size)
        // El forro de oro con chevrones.
        drawRect(c(ORO), b.topLeft, b.size)
        clipRect(l, top, r, top + H * 0.5f) {
            var y = top - 6 * u
            while (y < top + H * 0.5f) {
                drawPath(Path().apply { var x = l; moveTo(x, y); while (x < r) { lineTo(x + 4 * u, y + 3 * u); lineTo(x + 8 * u, y); x += 8 * u } }, c(ORO_CLARO).copy(alpha = 0.6f), style = Stroke(0.8f * u))
                y += 5 * u
            }
        }
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val hondo = H * 0.4f
        val solapa = solapaGradas(l, r, top, hondo)
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            drawPath(solapa, c(mezcla(ORO, LACA, 0.25f)))
            drawPath(solapa, c(ORO_CLARO), style = Stroke(0.8f * u))
        }
        // La carta, con su filete de oro.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.82f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 9 * u, 5.5f * u, 7 * u, oscuro)
            drawRect(c(ORO), Offset(h.left + 3 * u, h.top + 3 * u), Size(h.width - 6 * u, h.height - 6 * u), style = Stroke(0.5f * u))
            drawLine(c(JADE), Offset(h.center.x - 8 * u, h.top + 5.5f * u), Offset(h.center.x + 8 * u, h.top + 5.5f * u), 1 * u)
        }
        // El frente: laca negra con el doble filete y las esquinas en escalón.
        val v = top + H * 0.3f
        val frente = Path().apply {
            moveTo(l, top + H * 0.1f); lineTo(cx - W * 0.08f, v); lineTo(cx + W * 0.08f, v); lineTo(r, top + H * 0.1f); lineTo(r, bot); lineTo(l, bot); close()
        }
        drawPath(frente, c(LACA))
        clipPath(frente) {
            for (j in 0 until 4) { val x0 = l + W * (0.15f + 0.22f * j); drawLine(Color.White.copy(alpha = 0.035f), Offset(x0, top), Offset(x0 - W * 0.2f, bot), 7 * u) }
            val marco = Rect(l + 4 * u, top + 4 * u, r - 4 * u, bot - 4 * u)
            drawPath(escalonado(marco, 9 * u), c(ORO), style = Stroke(0.9f * u))
            drawPath(escalonado(marco.deflate(2.5f * u), 7 * u), c(ORO).copy(alpha = 0.6f), style = Stroke(0.5f * u))
            listOf(-1f, 1f).forEach { s ->
                val p0 = Offset(cx + s * W * 0.33f, bot - 10 * u)
                for (j in 0 until 3) drawLine(c(ORO).copy(alpha = 0.7f), Offset(p0.x, p0.y - j * 2.5f * u), Offset(p0.x + s * (12 - j * 3) * u, p0.y - j * 2.5f * u), 0.7f * u)
            }
        }
        drawPath(Path().apply { moveTo(l, top + H * 0.1f); lineTo(cx - W * 0.08f, v); lineTo(cx + W * 0.08f, v); lineTo(r, top + H * 0.1f) }, c(ORO).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        // La solapa escalonada, con su sol.
        val botonA = Offset(r - W * 0.2f, top + hondo * 0.16f)
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            translate(0f, 1.5f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.5f)) }
            drawPath(solapa, c(mezcla(LACA, ORO, 0.06f)))
            clipPath(solapa) {
                val sol = Offset(cx, top + hondo)
                for (i in 0 until 15) {
                    val a = (PI + PI * (i + 0.5f) / 15).toFloat()
                    drawLine(c(ORO).copy(alpha = if (i % 2 == 0) 0.75f else 0.35f), sol, Offset(sol.x + cos(a) * W * 0.7f, sol.y + sin(a) * W * 0.7f), (if (i % 2 == 0) 1f else 0.6f) * u)
                }
            }
            drawPath(solapa, c(ORO), style = Stroke(1 * u))
            if (e.cordon > 0f) botonCierre(botonA, 3.2f * u, oscuro)
        }
        // El cordón de oro en ocho, entre el botón de la solapa y el del sobre.
        val botonB = Offset(r - W * 0.2f, top + H * 0.52f)
        if (e.cordon > 0f) {
            botonCierre(botonB, 3.2f * u, oscuro)
            val a = Offset(botonA.x, top + (botonA.y - top) * k)
            val p = Path().apply {
                moveTo(botonB.x - 3 * u, botonB.y + 2 * u)
                for (j in 0 until 3) {
                    quadraticTo(botonB.x - 8 * u, (a.y + botonB.y) / 2, a.x + 3.5f * u, a.y - 2 * u)
                    quadraticTo(a.x - 4 * u, a.y - 4 * u, a.x - 3.5f * u, a.y + 1 * u)
                    quadraticTo(botonB.x + 8 * u, (a.y + botonB.y) / 2, botonB.x + 3.5f * u, botonB.y + 2 * u)
                    quadraticTo(botonB.x, botonB.y + 5 * u, botonB.x - 3.5f * u, botonB.y)
                }
                quadraticTo(botonB.x - 2 * u, botonB.y + 5 * u, botonB.x + 2 * u, botonB.y + 7 * u)
            }
            trazoParcialCo(p, e.cordon, c(ORO_CLARO), 1 * u)
        }
        // El medallón de esmalte.
        val selloR = H * 0.14f
        val y0 = top + hondo
        val selloC = Offset(cx, top + hondo * k)
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(JADE_LUZ), e.brillo)
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, selloC.y - y0 - selloR * 0.3f * e.romper),
                Offset(selloR * 0.25f * e.romper, selloR * 0.3f * e.romper), e.semilla) { medallon(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.25f, selloR * 0.3f), e.semilla, conArriba = false) { medallon(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        }
        textoEn(medidor, e.linea1, Rect(l + W * 0.1f, top + H * 0.62f, r - W * 0.1f, top + H * 0.78f), LetraExtra.poiret, c(CREMA), 12f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(l + W * 0.12f, top + H * 0.78f, r - W * 0.12f, top + H * 0.9f), letra, c(ORO_CLARO), 9f, minimo = 6f)
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.24f, 0.84f, 0.39f)

    override val altoBuzon: Dp = 210.dp

    /** Un sobre de laca chico, cayendo por el tubo de cristal: listo (el medallón brilla), atado con el cordón o abierto. */
    private fun DrawScope.sobreChico(r: Rect, tipo: Int, oscuro: Boolean, giro: Float) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        rotate(giro, r.center) {
            if (tipo == 2) {
                drawRect(c(NACAR), Offset(r.left + r.width * 0.1f, r.top - r.height * 0.5f), Size(r.width * 0.8f, r.height * 0.8f))
                for (j in 0 until 2) drawLine(c(LACA).copy(alpha = 0.4f), Offset(r.left + r.width * 0.2f, r.top - r.height * (0.32f - j * 0.18f)), Offset(r.right - r.width * 0.2f, r.top - r.height * (0.32f - j * 0.18f)), 0.6f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft + Offset(1 * u, 1 * u), r.size)
            drawRect(c(LACA), r.topLeft, r.size)
            drawRect(c(ORO), r.deflate(1.2f * u).topLeft, r.deflate(1.2f * u).size, style = Stroke(0.5f * u))
            val punta = if (tipo == 2) r.top - r.height * 0.45f else r.top + r.height * 0.5f
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, punta); lineTo(r.right, r.top) }, c(ORO), style = Stroke(0.8f * u))
            if (tipo == 1) {
                drawLine(c(ORO_CLARO), Offset(r.center.x + r.width * 0.25f, r.top + r.height * 0.2f), Offset(r.center.x + r.width * 0.25f, r.bottom - r.height * 0.15f), 0.8f * u)
                drawCircle(c(ORO), 1.6f * u, Offset(r.center.x + r.width * 0.25f, r.top + r.height * 0.2f))
                drawCircle(c(ORO), 1.6f * u, Offset(r.center.x + r.width * 0.25f, r.bottom - r.height * 0.15f))
            }
            if (tipo != 2) {
                drawCircle(c(ORO), r.height * 0.2f, Offset(r.center.x, r.top + r.height * 0.5f))
                drawCircle(c(if (tipo == 0) JADE_LUZ else JADE), r.height * 0.15f, Offset(r.center.x, r.top + r.height * 0.5f))
            }
        }
    }

    /**
     * El buzón del vestíbulo del rascacielos: el tubo de cristal del correo que baja por la pared de mármol verde entre sus
     * bandas de cromo, con los sobres de laca cayendo dentro (hasta diez): los listos, con el medallón de jade que brilla
     * (el primer degradado), los atados con su cordón de oro y los abiertos, con la carta asomando. Abajo, la caja de bronce
     * dorado con su remate escalonado, el sol de rayos, la boca «CORREO» y tres cartelas de laca con la cuenta de cada clase
     * en oro. A los lados, dos apliques de abanico; en modo oscuro se encienden sobre el vestíbulo a oscuras (el segundo
     * degradado). Abajo, el piso de terrazo en damero.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF07090B), 0.45f) else t(x, false, false)
        // La pared de mármol verde con sus vetas.
        drawRect(m(PARED))
        vetas(Rect(0f, 0f, w, h), m(mezcla(PARED, CREMA, 0.3f)).copy(alpha = 0.35f), 111, densidad = 0.25f, ondulacion = 3f)
        val piso = h - 16 * u
        // Los apliques de abanico.
        listOf(w * 0.1f, w * 0.9f).forEach { ax ->
            val base = Offset(ax, h * 0.32f)
            if (oscuro) mancha(base - Offset(0f, 8 * u), w * 0.22f, LUZ_LAMPARA.copy(alpha = 0.32f))
            drawRect(c(CROMO), Offset(ax - 6 * u, base.y), Size(12 * u, 4 * u))
            drawRect(c(CROMO_OSC), Offset(ax - 3 * u, base.y + 4 * u), Size(6 * u, 10 * u))
            val pant = if (oscuro) ESMERILADO else c(ESMERILADO)
            for (i in 0 until 5) drawArc(if (i % 2 == 0) pant else mezcla(pant, ORO, 0.2f), 180f + i * 36f, 36f, true, Offset(ax - 18 * u, base.y - 18 * u), Size(36 * u, 36 * u))
            for (i in 1 until 5) { val a = (PI + i * PI / 5).toFloat(); drawLine(c(CROMO), base, Offset(ax + cos(a) * 18 * u, base.y + sin(a) * 18 * u), 0.8f * u) }
        }
        // El tubo de cristal, con sus bandas de cromo.
        val tubo = Rect(w * 0.31f, 0f, w * 0.69f, h * 0.5f)
        drawRect(m(mezcla(PARED, CREMA, 0.22f)), tubo.topLeft, tubo.size)
        val cartas = cartasBuzonCo(e, 10)
        val sw = tubo.width * 0.3f; val sh = sw * 0.52f
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) { cartas.forEachIndexed { i, tipo ->
            val col = i % 3; val fila = i / 3
            val cx = tubo.left + tubo.width * (0.18f + 0.32f * col) + (azar(112, i) - 0.5f) * 6 * u
            val cy = tubo.bottom - sh * 0.7f - fila * (tubo.height - sh * 1.9f) / 3f
            val r = Rect(cx - sw / 2, cy - sh / 2, cx + sw / 2, cy + sh / 2)
            if (tipo == 0) mancha(r.center, sw * 0.6f, JADE_LUZ.copy(alpha = 0.5f))
            sobreChico(r, tipo, oscuro, (azar(113, i) - 0.5f) * 22f)
        } }
        // El cristal: reflejos y las bandas de cromo.
        drawRect(Color.White.copy(alpha = 0.08f), Offset(tubo.left + tubo.width * 0.1f, 0f), Size(tubo.width * 0.1f, tubo.height))
        drawRect(Color.White.copy(alpha = 0.05f), Offset(tubo.left + tubo.width * 0.75f, 0f), Size(tubo.width * 0.05f, tubo.height))
        cromo(Rect(tubo.left - 4 * u, 0f, tubo.left, tubo.bottom), oscuro, false)
        cromo(Rect(tubo.right, 0f, tubo.right + 4 * u, tubo.bottom), oscuro, false)
        listOf(h * 0.12f, h * 0.32f).forEach { y -> cromo(Rect(tubo.left - 4 * u, y, tubo.right + 4 * u, y + 3 * u), oscuro, false) }
        // La caja de bronce dorado.
        val caja = Rect(w * 0.16f, h * 0.5f, w * 0.84f, piso)
        val remate = Path().apply {
            moveTo(caja.left, caja.top + 12 * u); lineTo(caja.left + caja.width * 0.12f, caja.top + 12 * u); lineTo(caja.left + caja.width * 0.12f, caja.top + 6 * u)
            lineTo(caja.left + caja.width * 0.26f, caja.top + 6 * u); lineTo(caja.left + caja.width * 0.26f, caja.top); lineTo(caja.right - caja.width * 0.26f, caja.top)
            lineTo(caja.right - caja.width * 0.26f, caja.top + 6 * u); lineTo(caja.right - caja.width * 0.12f, caja.top + 6 * u); lineTo(caja.right - caja.width * 0.12f, caja.top + 12 * u)
            lineTo(caja.right, caja.top + 12 * u); lineTo(caja.right, caja.bottom); lineTo(caja.left, caja.bottom); close()
        }
        translate(3 * u, 3 * u) { drawPath(remate, Color.Black.copy(alpha = 0.35f)) }
        drawPath(remate, c(ORO))
        drawPath(remate, c(mezcla(ORO, Color.Black, 0.4f)), style = Stroke(1 * u))
        // El sol de rayos sobre la boca.
        val sol = Offset(caja.center.x, caja.top + 26 * u)
        for (i in 0 until 13) { val a = (PI + PI * (i + 0.5f) / 13).toFloat(); drawLine(c(ORO_CLARO), sol, Offset(sol.x + cos(a) * 20 * u, sol.y + sin(a) * 20 * u), 1.2f * u) }
        drawArc(c(JADE), 180f, 180f, true, Offset(sol.x - 6 * u, sol.y - 6 * u), Size(12 * u, 12 * u))
        val boca = Rect(caja.center.x - caja.width * 0.26f, sol.y + 2 * u, caja.center.x + caja.width * 0.26f, sol.y + 9 * u)
        drawRect(Color.Black, boca.topLeft, boca.size)
        drawRect(c(ORO_CLARO), boca.topLeft, boca.size, style = Stroke(1 * u))
        textoEn(medidor, "CORREO", Rect(boca.left, boca.bottom + 1 * u, boca.right, boca.bottom + 10 * u), LetraExtra.poiret, c(LACA), 8.5f, peso = FontWeight.Bold, espaciado = 3f, minimo = 5f)
        // Las tres cartelas de laca.
        val cuentas = listOf("POR ABRIR" to e.listas, "LACRADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val cw = (caja.width - 24 * u) / 3
        cuentas.forEachIndexed { i, (txt, n) ->
            val ct = Rect(caja.left + 6 * u + i * (cw + 6 * u), boca.bottom + 13 * u, caja.left + 6 * u + i * (cw + 6 * u) + cw, caja.bottom - 6 * u)
            drawPath(escalonado(ct, 5 * u), c(LACA))
            drawPath(escalonado(ct.deflate(1.5f * u), 4 * u), c(ORO), style = Stroke(0.6f * u))
            textoEn(medidor, txt, Rect(ct.left + 3 * u, ct.top + 2 * u, ct.right - 3 * u, ct.top + ct.height * 0.42f), letra, c(ORO_CLARO), 7.5f, espaciado = 0.6f, minimo = 4.5f)
            textoEn(medidor, "$n", Rect(ct.left + 3 * u, ct.top + ct.height * 0.38f, ct.right - 3 * u, ct.bottom - 2 * u), LetraExtra.poiret, if (i == 0 && n > 0) c(JADE_LUZ) else c(CREMA), 15f, peso = FontWeight.Bold, minimo = 6f)
        }
        // El piso de terrazo en damero.
        val lado = 16 * u
        var x = 0f; var i = 0
        while (x < w) { drawRect(m(if (i % 2 == 0) LINOLEO_A else LINOLEO_B), Offset(x, piso), Size(lado, h - piso)); x += lado; i++ }
        cromo(Rect(0f, piso - 2 * u, w, piso), oscuro, false)
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
 * La tapa es ese cuaderno de campo tumbado, con sus hojas prensadas asomando, la etiqueta «CUADERNO DE CAMPO», las
 * hormigas en seco, la correa con su broche y el lápiz en su presilla; al abrirlo, se ve un helecho prensado.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.27f, 0.305f, 0.77f, 0.675f)
    override val letraTapa: FontFamily = LetraExtra.fraunces
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El papel de la etiqueta de la tapa: hundido en oscuro y verdoso de noche, pero claro para el extracto. */
    private fun papelTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0A), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.13f)
        return r
    }

    /** Una hoja prensada: la lámina en punta, su nervio y dos pares de nervaduras. */
    private fun DrawScope.hojaPrensada(base: Offset, largo: Float, ancho: Float, giro: Float, color: Color, nervio: Color) {
        val u = density
        rotate(giro, base) {
            val punta = Offset(base.x, base.y - largo)
            drawPath(Path().apply {
                moveTo(base.x, base.y); quadraticTo(base.x - ancho, base.y - largo * 0.5f, punta.x, punta.y)
                quadraticTo(base.x + ancho, base.y - largo * 0.5f, base.x, base.y); close()
            }, color)
            drawLine(nervio, base, Offset(punta.x, punta.y + largo * 0.08f), 0.6f * u)
            listOf(0.35f, 0.6f).forEach { f ->
                val p = Offset(base.x, base.y - largo * f)
                drawLine(nervio, p, Offset(p.x - ancho * 0.5f, p.y - largo * 0.12f), 0.5f * u)
                drawLine(nervio, p, Offset(p.x + ancho * 0.5f, p.y - largo * 0.12f), 0.5f * u)
            }
            drawLine(nervio, base, Offset(base.x, base.y + largo * 0.12f), 0.8f * u)
        }
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val cu = Rect(w * 0.025f, h * 0.09f, w * 0.935f, h * 0.95f)
        val bisagra = cu.left + w * 0.065f
        drawRoundRect(Color.Black.copy(alpha = 0.2f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(3 * u))
        // Las hojas prensadas entre las páginas, que asoman por arriba (más cuanto más escribiste), y una fronda de helecho por el canto.
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val hojas = 1 + (lleno * 3).toInt()
        val colores = listOf(HOJA_CLARA, OTONO, HELECHO, MUSGO)
        for (j in 0 until hojas) {
            val hx = w * (0.34f + 0.12f * j) + w * 0.03f * azar(e.semilla, 70 + j)
            val largo = h * (0.08f + 0.03f * azar(e.semilla, 80 + j))
            hojaPrensada(Offset(hx, cu.top + h * 0.03f), largo, largo * 0.32f, -25f + 50f * azar(e.semilla, 90 + j), c(colores[(j + (e.semilla and 3)) % 4]), c(ROBLE_VETA).copy(alpha = 0.6f))
        }
        fronda(Offset(w * 0.2f, cu.top + h * 0.05f), h * 0.12f, -18f, c(HELECHO))
        // El canto de las hojas.
        drawRect(c(PAPEL), Offset(cu.left + 3 * u, cu.top + 3 * u), cu.size)
        for (k in 1..3) drawLine(c(ROBLE_VETA).copy(alpha = 0.3f), Offset(cu.left + 8 * u, cu.bottom + k * 0.7f * u), Offset(cu.right + 2 * u, cu.bottom + k * 0.7f * u), 0.4f * u)
        // Por dentro (se ve al abrirse): la hoja del cuaderno con un helecho prensado sujeto con dos tiras de papel engomado.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(PAPEL), hoja.topLeft, hoja.size)
            var ly = hoja.top + 14 * u
            while (ly < hoja.bottom - 4 * u) { drawLine(c(HOJA_CLARA).copy(alpha = 0.3f), Offset(hoja.left + hoja.width * 0.5f, ly), Offset(hoja.right - 8 * u, ly), 0.5f * u); ly += 7 * u }
            fronda(Offset(hoja.left + hoja.width * 0.26f, hoja.bottom - 10 * u), hoja.height * 0.75f, 8f, c(HELECHO))
            listOf(0.3f, 0.82f).forEach { f ->
                rotate(-12f, Offset(hoja.left + hoja.width * 0.26f, hoja.top + hoja.height * f)) {
                    drawRect(c(OTONO).copy(alpha = 0.35f), Offset(hoja.left + hoja.width * 0.18f, hoja.top + hoja.height * f - 3 * u), Size(hoja.width * 0.16f, 6 * u))
                }
            }
        }
        // La tapa de tela, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(tela, cu.topLeft, cu.size, CornerRadius(3 * u))
            // La trama de la tela, en los dos sentidos.
            var k = cu.left
            while (k < cu.right) { drawLine(Color.White.copy(alpha = 0.04f), Offset(k, cu.top), Offset(k, cu.bottom), 0.5f * u); k += 2.2f * u }
            var q = cu.top
            while (q < cu.bottom) { drawLine(Color.Black.copy(alpha = 0.05f), Offset(cu.left, q), Offset(cu.right, q), 0.5f * u); q += 2.2f * u }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cu.right - 4 * u, cu.top), Size(4 * u, cu.height))
            // Las filas de hormigas estampadas en seco, arriba y abajo.
            var xx = bisagra + 14 * u
            while (xx < w * 0.83f) {
                listOf(cu.top + 8 * u, cu.bottom - 8 * u).forEach { y ->
                    drawCircle(Color.Black.copy(alpha = 0.22f), 2.2f * u, Offset(xx - 4 * u, y)); drawCircle(Color.Black.copy(alpha = 0.22f), 1.3f * u, Offset(xx, y))
                    drawCircle(Color.Black.copy(alpha = 0.22f), 1.7f * u, Offset(xx + 3.4f * u, y))
                    listOf(-1f, 1f).forEach { s -> drawLine(Color.Black.copy(alpha = 0.2f), Offset(xx, y), Offset(xx + s * 2.2f * u, y + 2.6f * u), 0.5f * u) }
                }
                xx += 22 * u
            }
            // La hoja estampada en seco a la izquierda; de noche, una luciérnaga posada.
            val ch = Offset((bisagra + w * 0.235f) / 2, cu.center.y)
            val s = minOf(h * 0.15f, (w * 0.235f - bisagra) * 0.6f)
            if (e.noche) {
                luciernaga(ch, 2.4f * u)
            } else {
                val emblema = Path().apply {
                    moveTo(ch.x, ch.y + s); quadraticTo(ch.x - s * 0.7f, ch.y, ch.x, ch.y - s); quadraticTo(ch.x + s * 0.7f, ch.y, ch.x, ch.y + s); close()
                }
                drawPath(emblema, Color.Black.copy(alpha = 0.2f))
                drawPath(emblema, Color.White.copy(alpha = 0.1f), style = Stroke(0.7f * u))
                drawLine(Color.White.copy(alpha = 0.14f), Offset(ch.x, ch.y + s * 1.25f), Offset(ch.x, ch.y - s * 0.85f), 0.7f * u)
                for (j in 1..3) {
                    val p = Offset(ch.x, ch.y + s * (0.5f - j * 0.32f))
                    drawLine(Color.White.copy(alpha = 0.1f), p, Offset(p.x - s * 0.3f, p.y - s * 0.22f), 0.5f * u)
                    drawLine(Color.White.copy(alpha = 0.1f), p, Offset(p.x + s * 0.3f, p.y - s * 0.22f), 0.5f * u)
                }
            }
            // La etiqueta de papel con su doble filete: el rótulo, el extracto, la fecha y las palabras.
            val et = Rect(w * 0.235f, h * 0.165f, w * 0.805f, h * 0.875f)
            val papel = papelTapa(oscuro, e.noche)
            drawRect(Color.Black.copy(alpha = 0.25f), et.topLeft + Offset(1.2f * u, 1.8f * u), et.size)
            drawRect(papel, et.topLeft, et.size)
            drawRect(c(TINTA).copy(alpha = 0.6f), et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.8f * u))
            drawRect(c(TINTA).copy(alpha = 0.4f), et.deflate(4.5f * u).topLeft, et.deflate(4.5f * u).size, style = Stroke(0.5f * u))
            textoEn(medidor, "CUADERNO DE CAMPO", Rect(et.left + 22 * u, h * 0.19f, et.right - 22 * u, h * 0.265f), letra, c(TINTA).copy(alpha = 0.8f), 9f,
                peso = FontWeight.SemiBold, espaciado = 1.6f, minimo = 6f)
            listOf(et.left + 14 * u to -30f, et.right - 14 * u to 30f).forEach { (x, g) ->
                hojaPrensada(Offset(x, h * 0.25f), h * 0.055f, h * 0.02f, g, c(HELECHO).copy(alpha = 0.8f), papel.copy(alpha = 0.5f))
            }
            drawLine(c(TINTA).copy(alpha = 0.4f), Offset(et.left + et.width * 0.2f, h * 0.285f), Offset(et.right - et.width * 0.2f, h * 0.285f), 0.6f * u)
            drawLine(c(TINTA).copy(alpha = 0.4f), Offset(et.left + et.width * 0.2f, h * 0.69f), Offset(et.right - et.width * 0.2f, h * 0.69f), 0.6f * u)
            textoEn(medidor, e.fecha, Rect(et.left + 8 * u, h * 0.7f, et.right - 8 * u, h * 0.778f), letra, c(TINTA), 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(et.left + 8 * u, h * 0.782f, et.right - 8 * u, h * 0.845f), letra, c(TINTA).copy(alpha = 0.7f), 9f, minimo = 6f)
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(3 * u))
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo, más oscuro, con su pespunte.
        drawRect(tela, cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.Black.copy(alpha = 0.28f), cu.topLeft, Size(bisagra - cu.left, cu.height))
        var py = cu.top + 4 * u
        while (py < cu.bottom - 4 * u) { drawLine(c(PAPEL).copy(alpha = 0.4f), Offset(bisagra - 3 * u, py), Offset(bisagra - 3 * u, py + 2.5f * u), 0.6f * u); py += 5 * u }
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 1 * u)
        // La correa con su broche de latón y el lápiz en su presilla, junto al canto: al abrirse, se sueltan.
        val suelto = (1f - a * 2.2f).coerceAtLeast(0f)
        if (suelto > 0f) {
            val xc = w * 0.85f + a * w * 0.1f
            drawRect(Color.Black.copy(alpha = 0.25f * suelto), Offset(xc + 1.4f * u, cu.top), Size(w * 0.035f, cu.height))
            drawRect(c(ROBLE_VETA).copy(alpha = suelto), Offset(xc, cu.top - 1.5f * u), Size(w * 0.035f, cu.height + 3 * u))
            drawLine(c(ROBLE_LUZ).copy(alpha = 0.6f * suelto), Offset(xc + 1.2f * u, cu.top), Offset(xc + 1.2f * u, cu.bottom), 0.6f * u)
            var cy = cu.top + 3 * u
            while (cy < cu.bottom - 2 * u) { drawLine(c(ROBLE_LUZ).copy(alpha = 0.35f * suelto), Offset(xc + w * 0.031f, cy), Offset(xc + w * 0.031f, cy + 2 * u), 0.5f * u); cy += 4 * u }
            val br = Offset(xc + w * 0.0175f, cu.center.y)
            drawCircle(c(LATON).copy(alpha = suelto), 5 * u, br)
            drawCircle(c(ROBLE_VETA).copy(alpha = 0.5f * suelto), 5 * u, br, style = Stroke(0.7f * u))
            drawCircle(Color.White.copy(alpha = 0.35f * suelto), 1.6f * u, Offset(br.x - 1.5f * u, br.y - 1.5f * u))
            // El lápiz verde, con su virola y su goma, sujeto al canto por dos presillas.
            val xl = cu.right + 1 * u + a * w * 0.04f
            val lp = Rect(xl, h * 0.15f, xl + w * 0.024f, h * 0.83f)
            drawRect(c(MUSGO).copy(alpha = suelto), lp.topLeft, lp.size)
            drawLine(Color.White.copy(alpha = 0.2f * suelto), Offset(lp.left + lp.width * 0.3f, lp.top), Offset(lp.left + lp.width * 0.3f, lp.bottom), 0.6f * u)
            drawLine(Color.Black.copy(alpha = 0.2f * suelto), Offset(lp.left + lp.width * 0.7f, lp.top), Offset(lp.left + lp.width * 0.7f, lp.bottom), 0.6f * u)
            drawRect(c(LATON).copy(alpha = suelto), Offset(lp.left, lp.top - h * 0.03f), Size(lp.width, h * 0.03f))
            drawRoundRect(c(BARRO).copy(alpha = suelto), Offset(lp.left, lp.top - h * 0.065f), Size(lp.width, h * 0.04f), CornerRadius(1.5f * u))
            drawPath(Path().apply { moveTo(lp.left, lp.bottom); lineTo(lp.right, lp.bottom); lineTo(lp.center.x, lp.bottom + h * 0.08f); close() }, c(ROBLE_LUZ).copy(alpha = suelto))
            drawPath(Path().apply { moveTo(lp.center.x - lp.width * 0.18f, lp.bottom + h * 0.055f); lineTo(lp.center.x + lp.width * 0.18f, lp.bottom + h * 0.055f); lineTo(lp.center.x, lp.bottom + h * 0.08f); close() }, c(TINTA).copy(alpha = suelto))
            listOf(0.3f, 0.68f).forEach { f -> drawRect(c(OTONO).copy(alpha = suelto), Offset(cu.right - 3 * u, h * f), Size(lp.right - cu.right + 4 * u, h * 0.05f)) }
        }
        // De noche vuelan luciérnagas alrededor del cuaderno.
        if (e.noche) clipRect {
            listOf(0.05f to 0.2f, 0.16f to 0.97f, 0.62f to 0.05f, 0.9f to 0.05f, 0.99f to 0.93f, 0.08f to 0.7f, 0.4f to 0.98f).forEachIndexed { j, (fx, fy) ->
                luciernaga(Offset(w * fx, h * fy), (1.2f + 0.8f * azar(e.semilla, 400 + j)) * u)
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    /** El papel kraft de las tiras engomadas que sujetan los ejemplares. */
    private val KRAFT = Color(0xFFC9A877)

    override val altoEncabezado: Dp = 120.dp

    /**
     * La etiqueta de herbario: a la izquierda, una fronda de helecho prensada con una hoja de otoño; a la derecha, la
     * etiqueta de doble filete, sujeta con dos tiras engomadas, con «Lámina … · observaciones de campo», la fecha y el
     * detalle. De noche, la luna sobre el helecho y luciérnagas alrededor.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        // El ejemplar prensado.
        fronda(Offset(22 * u, h - 8 * u), 92 * u, 8f, if (e.noche) mezcla(p.acento, p.tinta, 0.2f).copy(alpha = 0.8f) else p.acento)
        hojaPrensada(Offset(30 * u, h - 10 * u), 26 * u, 9 * u, 38f, c(OTONO).copy(alpha = 0.85f), c(TINTA).copy(alpha = 0.35f))
        drawRect(c(KRAFT).copy(alpha = 0.75f), Offset(13 * u, h - 22 * u), Size(22 * u, 7 * u))
        if (e.noche) {
            crecienteCon(Offset(13 * u, 14 * u), 7 * u, p.acento)
            listOf(Offset(36f, 22f), Offset(8f, 48f), Offset(40f, 66f), Offset(14f, 84f)).forEach { k -> luciernaga(Offset(k.x * u, k.y * u), 1.6f * u) }
        }
        // La etiqueta de herbario.
        val et = Rect(50 * u, 8 * u, w - 2 * u, h - 5 * u)
        val fondo = if (p.oscura) mezcla(p.color, p.tinta, 0.06f) else mezcla(papelVistoCon(p, oscuro), Color.White, if (oscuro) 0.06f else 0.35f)
        drawRect(Color.Black.copy(alpha = 0.1f), et.topLeft + Offset(1 * u, 1.5f * u), et.size)
        drawRect(fondo, et.topLeft, et.size)
        drawRect(p.tinta.copy(alpha = 0.55f), et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.9f * u))
        drawRect(p.tinta.copy(alpha = 0.3f), et.deflate(4.5f * u).topLeft, et.deflate(4.5f * u).size, style = Stroke(0.5f * u))
        listOf(Offset(et.left - 4 * u, et.top - 3 * u) to -12f, Offset(et.right - 22 * u, et.top - 3 * u) to 10f).forEach { (o, g) ->
            rotate(g, o + Offset(13 * u, 3.5f * u)) { drawRect(c(KRAFT).copy(alpha = 0.7f), o, Size(26 * u, 7 * u)) }
        }
        textoEn(medidor, e.rotulo, Rect(et.left + 8 * u, et.top + 7 * u, et.right - 8 * u, et.top + 23 * u), LetraExtra.fraunces, p.acento, 12.5f, minimo = 7f)
        drawLine(p.tinta.copy(alpha = 0.35f), Offset(et.left + 12 * u, et.top + 26 * u), Offset(et.right - 12 * u, et.top + 26 * u), 0.6f * u)
        fechaCon(medidor, e.fecha, Rect(et.left + 8 * u, et.top + 29 * u, et.right - 8 * u, et.top + 74 * u), LetraExtra.fraunces, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(et.left + 8 * u, et.top + 76 * u, et.right - 8 * u, et.bottom - 6 * u), LetraExtra.fraunces, p.tenue, 11f, minimo = 6.5f)
    }

    /**
     * La lámina del herbario: el doble marco, un helecho prensado grande y casi transparente abajo a la izquierda, sujeto con
     * tiras engomadas, la etiqueta del colector abajo a la derecha con sus renglones y el sello redondo, y ramitas en las
     * esquinas de arriba. En modo oscuro, la luna entra por los vidrios de la caja de Ward, arriba a la derecha, con la sombra
     * de sus parteluces.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        drawRect(p.tinta.copy(alpha = 0.35f), Offset(12 * u, 12 * u), Size(w - 24 * u, h - 24 * u), style = Stroke(1.2f * u))
        drawRect(p.tinta.copy(alpha = 0.2f), Offset(16 * u, 16 * u), Size(w - 32 * u, h - 32 * u), style = Stroke(0.8f * u))
        // El ejemplar prensado.
        val base = Offset(w * 0.24f, h - 28 * u)
        fronda(base, h * 0.3f, -10f, p.acento.copy(alpha = 0.13f))
        fronda(base + Offset(10 * u, 0f), h * 0.22f, 14f, p.acento.copy(alpha = 0.1f))
        hojaPrensada(base + Offset(-6 * u, -4 * u), 46 * u, 16 * u, -40f, c(OTONO).copy(alpha = 0.12f), c(TINTA).copy(alpha = 0.08f))
        listOf(base + Offset(-12 * u, -22 * u) to -8f, base + Offset(-2 * u, -h * 0.16f) to 12f).forEach { (o, g) ->
            rotate(g, o + Offset(14 * u, 4 * u)) { drawRect(c(KRAFT).copy(alpha = 0.3f), o, Size(28 * u, 8 * u)) }
        }
        // La etiqueta del colector.
        val et = Rect(w - 22 * u - 112 * u, h - 22 * u - 54 * u, w - 22 * u, h - 22 * u)
        drawRect(p.tinta.copy(alpha = 0.25f), et.topLeft, et.size, style = Stroke(0.9f * u))
        for (k in 0 until 3) drawLine(p.tinta.copy(alpha = 0.15f), Offset(et.left + 8 * u, et.top + (16 + k * 12) * u), Offset(et.right - 34 * u, et.top + (16 + k * 12) * u), 0.6f * u)
        drawCircle(p.detalle.copy(alpha = 0.22f), 11 * u, Offset(et.right - 17 * u, et.center.y + 2 * u), style = Stroke(1 * u))
        drawCircle(p.detalle.copy(alpha = 0.15f), 7.5f * u, Offset(et.right - 17 * u, et.center.y + 2 * u), style = Stroke(0.6f * u))
        // Las ramitas de las esquinas de arriba.
        listOf(Offset(22 * u, 24 * u) to 130f, Offset(w - 22 * u, 24 * u) to -130f).forEach { (o, g) ->
            for (k in 0 until 3) hojaPrensada(o, 15 * u, 5 * u, g + (k - 1) * 30f, p.acento.copy(alpha = 0.2f), p.acento.copy(alpha = 0.1f))
        }
        if (oscuro && !p.oscura) {
            val luna = Offset(w * 0.86f, h * 0.02f)
            val luz = Color(0xFFDCEFD8)
            penumbraCon(luna, luz, 0.15f)
            // La sombra de los parteluces de la caja de Ward sobre la parte alumbrada.
            clipRect {
                rotate(-24f, luna) {
                    for (k in 0 until 4) drawRect(Color.Black.copy(alpha = 0.05f), Offset(luna.x - 30 * u - k * 72 * u, luna.y), Size(3.5f * u, h * 0.6f))
                    drawRect(Color.Black.copy(alpha = 0.04f), Offset(luna.x - w, luna.y + h * 0.22f), Size(w * 1.4f, 3.5f * u))
                }
            }
        }
    }

    /** La tira de papel engomado al pie de la barra, con sus fibras. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        val r = Rect(0f, h - 9 * u, w, h - 2.5f * u)
        drawRect(c(KRAFT).copy(alpha = 0.62f), r.topLeft, r.size)
        drawLine(c(ROBLE_VETA).copy(alpha = 0.25f), Offset(0f, r.bottom), Offset(w, r.bottom), 0.6f * u)
        for (k in 0 until (w / (9 * u)).toInt()) {
            val x = w * azar(61, k); val y = r.top + r.height * azar(61, k + 300)
            drawLine(c(ROBLE_VETA).copy(alpha = 0.18f), Offset(x, y), Offset(x + (2 + 4 * azar(61, k + 600)) * u, y + 0.6f * u), 0.5f * u)
        }
        hojaPrensada(Offset(w * 0.5f - 8 * u, r.top + 2 * u), 9 * u, 3.2f * u, -70f, p.acento.copy(alpha = 0.6f), p.acento.copy(alpha = 0.3f))
        hojaPrensada(Offset(w * 0.5f + 8 * u, r.top + 2 * u), 9 * u, 3.2f * u, 70f, p.acento.copy(alpha = 0.6f), p.acento.copy(alpha = 0.3f))
    }

    /** La etiqueta de tela verde del cuaderno de campo, con su pespunte crema y una hojita en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1 * u, 1.8f * u), size, CornerRadius(5 * u))
        drawRoundRect(c(TELAS[0]), Offset.Zero, size, CornerRadius(5 * u))
        var x = 2 * u
        while (x < w) { drawLine(Color.White.copy(alpha = 0.05f), Offset(x, 1 * u), Offset(x, h - 1 * u), 0.5f * u); x += 2 * u }
        drawRoundRect(c(PAPEL).copy(alpha = 0.75f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), CornerRadius(3 * u),
            style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.2f * u))))
        hojaPrensada(Offset(7 * u, h / 2 + 4 * u), 8 * u, 3 * u, 20f, c(HOJA_CLARA), c(TELAS[0]))
        hojaPrensada(Offset(w - 7 * u, h / 2 + 4 * u), 8 * u, 3 * u, -20f, c(HOJA_CLARA), c(TELAS[0]))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF1EAD6)

    // ---------------------------------------------------------------- la libreta de momentos

    /** El cartón oliva de la carpeta del herbario, la cinta de algodón, la lata verde del vasculum y la luz del farol. */
    private val CARTON = Color(0xFF6E7A4E)
    private val CINTA_ALGODON = Color(0xFFEFE2C0)
    private val LATA = Color(0xFF4E6B4A)
    private val FAROL_CAMPO = Color(0xFFFFD98A)

    override val altoLibreta: Dp = 256.dp

    /** Una hoja simple con su nervio: de [base] hacia el ángulo [a] (grados), de [largo]. */
    private fun DrawScope.hojaSimple(base: Offset, a: Float, largo: Float, color: Color, nervio: Color) {
        val u = density
        rotate(a, base) {
            val p = Path().apply {
                moveTo(base.x, base.y)
                quadraticTo(base.x + largo * 0.5f, base.y - largo * 0.32f, base.x + largo, base.y)
                quadraticTo(base.x + largo * 0.5f, base.y + largo * 0.32f, base.x, base.y); close()
            }
            drawPath(p, color)
            drawLine(nervio, base, Offset(base.x + largo * 0.92f, base.y), 0.6f * u)
        }
    }

    /** Una fronda de helecho prensada, de pinnas llenas: el raquis desde [base] y sus pinnas alternas, que se achican hacia la punta. */
    private fun DrawScope.frondaPrensada(base: Offset, largo: Float, a: Float, color: Color) {
        val u = density
        rotate(a, base) {
            drawLine(color, base, Offset(base.x, base.y - largo), 1 * u)
            for (k in 1..9) {
                val y = base.y - largo * k / 10f
                val l = largo * 0.28f * (1f - k / 11f)
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(base.x, y); quadraticTo(base.x + s * l * 0.5f, y - l * 0.35f, base.x + s * l, y - l * 0.15f)
                        quadraticTo(base.x + s * l * 0.5f, y + l * 0.1f, base.x, y); close() }, color)
                }
            }
        }
    }

    /**
     * El herbario de momentos sobre la mesa de campo de roble, en el invernadero: detrás, los vidrios esmerilados con sus
     * parteluces de hierro verde y la sombra de las hojas de afuera. A la izquierda, la prensa de plantas (dos rejillas de
     * listones con sus correas, abultada de pliegos) y el farol de campo; a la derecha, el vasculum de lata verde con su
     * correa, la lupa y una etiqueta suelta. El herbario es una carpeta de cartón oliva con la ventana donde se ve un helecho
     * prensado, la etiqueta del nombre y la cuenta, y la cinta de algodón anudada en lazo; engorda con las hojas y por los
     * cantos asoman hojas prensadas (una cada 12 hojas, hasta 9). Al abrirse, el lazo se deshace y la tapa se abate hacia
     * delante sobre su canto de abajo: dentro, el pliego con la fronda montada con tiras de goma y su etiqueta de herbario. En
     * modo oscuro, el invernadero de noche: alumbran el farol (el único degradado) y unas luciérnagas.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto, 0.3f)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF070A08), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF070A08), 0.3f) else x
        // Los vidrios del invernadero, con la sombra de las hojas de afuera y los parteluces.
        val mesaY = h * 0.5f
        drawRect(c(VIDRIO), Offset.Zero, Size(w, mesaY))
        for (k in 0 until 9) frondaPrensada(Offset(w * (0.05f + 0.12f * k), mesaY + 10 * u), (50f + 40f * azar(150, k)) * u, -20f + 40f * azar(151, k), c(HELECHO).copy(alpha = 0.18f))
        var x = 0f
        while (x <= w) { drawRect(c(HIERRO), Offset(x - 2 * u, 0f), Size(4 * u, mesaY)); x += w / 4 }
        drawRect(c(HIERRO), Offset(0f, mesaY * 0.45f), Size(w, 3 * u))
        for (k in 0 until 14) drawCircle(Color.White.copy(alpha = 0.35f), (0.8f + 1.2f * azar(152, k)) * u, Offset(w * azar(153, k), mesaY * 0.9f * azar(154, k)))
        // La mesa de campo de roble.
        drawRect(c(ROBLE), Offset(0f, mesaY), Size(w, h - mesaY))
        vetas(Rect(0f, mesaY, w, h), c(ROBLE_VETA).copy(alpha = 0.6f), 155, densidad = 0.9f)
        drawRect(c(ROBLE_LUZ), Offset(0f, mesaY), Size(w, 2.5f * u))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, mesaY + 2.5f * u), Size(w, 3 * u))
        // La prensa de plantas, a la izquierda: dos rejillas de listones, los pliegos y las correas.
        val pr = Rect(w * 0.015f, h * 0.56f, w * 0.2f, h * 0.9f)
        drawRect(Color.Black.copy(alpha = 0.2f), pr.topLeft + Offset(3 * u, 4 * u), pr.size)
        for (k in 0 until 6) drawRect(c(if (k % 2 == 0) PAPEL else mezcla(PAPEL, TIERRA, 0.2f)), Offset(pr.left + 2 * u, pr.bottom - 3 * u + k * 1.8f * u), Size(pr.width - 4 * u, 1.8f * u))
        listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawRect(c(ROBLE_LUZ), Offset(pr.left, pr.top + pr.height * f - 2 * u), Size(pr.width, 4 * u)) }
        listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawRect(c(ROBLE), Offset(pr.left + pr.width * f - 2 * u, pr.top), Size(4 * u, pr.height)) }
        drawRect(c(ROBLE_VETA), pr.topLeft, pr.size, style = Stroke(2.5f * u))
        listOf(0.36f, 0.64f).forEach { f -> drawRect(c(Color(0xFF5A3A22)), Offset(pr.left - 2 * u, pr.top + pr.height * f - 3 * u), Size(pr.width + 4 * u, 6 * u))
            drawRect(c(LATON), Offset(pr.right - 10 * u, pr.top + pr.height * f - 4 * u), Size(6 * u, 8 * u), style = Stroke(1.2f * u)) }
        // El farol de campo, sobre la mesa detrás de la prensa.
        val fa = Offset(w * 0.11f, h * 0.36f)
        drawArc(c(HIERRO), 180f, 180f, false, Offset(fa.x - 6 * u, fa.y - 26 * u), Size(12 * u, 10 * u), style = Stroke(1.4f * u))
        drawRect(c(HIERRO), Offset(fa.x - 9 * u, fa.y - 21 * u), Size(18 * u, 4 * u))
        drawRoundRect(if (oscuro) FAROL_CAMPO.copy(alpha = 0.7f) else c(VIDRIO).copy(alpha = 0.8f), Offset(fa.x - 8 * u, fa.y - 17 * u), Size(16 * u, 24 * u), CornerRadius(6 * u))
        drawPath(Path().apply { moveTo(fa.x, fa.y + 2 * u); quadraticTo(fa.x - 3 * u, fa.y - 3 * u, fa.x, fa.y - 9 * u); quadraticTo(fa.x + 3 * u, fa.y - 3 * u, fa.x, fa.y + 2 * u) },
            if (oscuro) Color(0xFFFFF4C8) else c(OTONO))
        listOf(-8f, 8f).forEach { dx -> drawLine(c(HIERRO), Offset(fa.x + dx * u, fa.y - 17 * u), Offset(fa.x + dx * u, fa.y + 7 * u), 1.2f * u) }
        drawRect(c(HIERRO), Offset(fa.x - 10 * u, fa.y + 7 * u), Size(20 * u, 5 * u))
        // A la derecha: el vasculum de lata con su correa, la lupa y una etiqueta suelta.
        val va = Rect(w * 0.8f, h * 0.54f, w * 0.99f, h * 0.7f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(va.left + 3 * u, va.bottom), Size(va.width, 4 * u))
        drawRoundRect(c(LATA), va.topLeft, va.size, CornerRadius(va.height / 2))
        drawRect(c(mezcla(LATA, Color.White, 0.2f)), Offset(va.left + va.height * 0.4f, va.top + va.height * 0.18f), Size(va.width - va.height * 0.8f, 3 * u))
        drawRect(c(HIERRO), Offset(va.left + va.width * 0.3f, va.top), Size(va.width * 0.4f, va.height), style = Stroke(1 * u))
        drawPath(Path().apply { moveTo(va.left + 6 * u, va.center.y); quadraticTo(va.center.x, va.top - 26 * u, va.right - 6 * u, va.center.y) }, c(Color(0xFF5A3A22)), style = Stroke(2.4f * u))
        val lu = Offset(w * 0.86f, h * 0.84f)
        drawLine(c(ROBLE_VETA), lu + Offset(9 * u, 9 * u), lu + Offset(24 * u, 20 * u), 4 * u)
        drawCircle(c(LATON), 12 * u, lu, style = Stroke(2.2f * u))
        drawCircle(c(VIDRIO).copy(alpha = 0.4f), 11 * u, lu)
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(lu.x - 8 * u, lu.y - 8 * u), Size(16 * u, 16 * u), style = Stroke(1.4f * u))
        rotate(-12f, Offset(w * 0.06f, h * 0.95f)) { drawRect(c(PAPEL), Offset(w * 0.03f, h * 0.92f), Size(26 * u, 12 * u)); drawLine(c(TINTA).copy(alpha = 0.4f), Offset(w * 0.03f + 4 * u, h * 0.92f + 6 * u), Offset(w * 0.03f + 20 * u, h * 0.92f + 6 * u), 0.7f * u) }
        // La luz del farol y las luciérnagas.
        if (oscuro) {
            drawRect(Brush.radialGradient(listOf(FAROL_CAMPO.copy(alpha = 0.34f), FAROL_CAMPO.copy(alpha = 0.1f), Color.Transparent), Offset(fa.x, fa.y - 5 * u), w * 0.85f))
            for (k in 0 until 7) {
                val p = Offset(w * azar(156, k), h * 0.4f * azar(157, k) + 6 * u)
                drawCircle(LUCIERNAGA.copy(alpha = 0.25f), 4 * u, p); drawCircle(LUCIERNAGA, 1.4f * u, p)
            }
        }
        // El herbario.
        val ancho = w * 0.56f; val alto = minOf(ancho * 0.7f, h * 0.53f)
        val r = Rect(w * 0.23f, h * 0.33f, w * 0.23f + ancho, h * 0.33f + alto)
        val g = (3f + 13f * lleno) * u
        val bisagra = r.bottom
        rotate(-1.5f, r.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), r.topLeft + Offset(5 * u, 5 * u + g), r.size)
            // Las hojas prensadas que asoman por los cantos.
            val n = (1 + e.hojas / 12).coerceAtMost(9)
            for (k in 0 until n) {
                val col = c(listOf(HELECHO, HOJA_CLARA, OTONO, MUSGO, BEGONIA)[(k + e.semilla.and(7)) % 5])
                if (k % 2 == 0) hojaSimple(Offset(r.right - 8 * u, r.top + alto * (0.15f + 0.09f * k)), -15f + 30f * azar(e.semilla, 160 + k), (16f + 8f * azar(e.semilla, 170 + k)) * u, col, c(TINTA).copy(alpha = 0.3f))
                else hojaSimple(Offset(r.left + ancho * (0.15f + 0.08f * k), r.top + 8 * u), -100f + 30f * azar(e.semilla, 160 + k), (14f + 8f * azar(e.semilla, 170 + k)) * u, col, c(TINTA).copy(alpha = 0.3f))
            }
            drawRect(c(mezcla(CARTON, Color.Black, 0.3f)), r.topLeft + Offset(0f, g), r.size)
            cantoLibreta(r, g * 0.85f, c(PAPEL), c(ROBLE_VETA).copy(alpha = 0.4f))
            // Por dentro: el pliego con la fronda montada y su etiqueta de herbario.
            if (abre > 0.01f) {
                drawRect(cp(PAPEL), r.topLeft, r.size)
                val fb = Offset(r.left + ancho * 0.4f, r.bottom - alto * 0.12f)
                frondaPrensada(fb, alto * 0.72f, 12f, c(HELECHO))
                listOf(0.3f, 0.6f).forEach { f -> drawRect(c(PAPEL).copy(alpha = 0.9f), Offset(fb.x - 8 * u + alto * 0.72f * f * 0.21f, fb.y - alto * 0.72f * f - 2 * u), Size(16 * u, 4 * u)) }
                val et = Rect(r.right - ancho * 0.36f, r.bottom - alto * 0.3f, r.right - 8 * u, r.bottom - 8 * u)
                drawRect(c(TINTA).copy(alpha = 0.6f), et.topLeft, et.size, style = Stroke(0.8f * u))
                for (k in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.4f), Offset(et.left + 5 * u, et.top + (6 + k * 7) * u), Offset(et.right - (5 + k * 8) * u, et.top + (6 + k * 7) * u), 0.7f * u)
            }
            // La tapa de cartón, que se abate hacia delante sobre su canto de abajo.
            val sy = 1f - abre
            if (sy > 0.02f) scale(1f, sy, Offset(r.center.x, bisagra)) {
                drawRect(c(CARTON), r.topLeft, r.size)
                for (k in 0 until 60) drawCircle(Color.Black.copy(alpha = 0.07f), 0.7f * u, Offset(r.left + ancho * azar(158, k), r.top + alto * azar(159, k)))
                drawRect(c(mezcla(CARTON, Color.Black, 0.25f)), Offset(r.left, r.bottom - 7 * u), Size(ancho, 7 * u))
                // La ventana con el helecho prensado.
                val ve = Rect(r.left + ancho * 0.06f, r.top + alto * 0.1f, r.left + ancho * 0.3f, r.top + alto * 0.84f)
                drawRect(cp(PAPEL), ve.topLeft, ve.size)
                drawRect(c(mezcla(CARTON, Color.Black, 0.4f)), ve.topLeft, ve.size, style = Stroke(1.6f * u))
                frondaPrensada(Offset(ve.center.x, ve.bottom - 6 * u), ve.height * 0.82f, 6f, c(HELECHO))
                // La etiqueta del nombre y la cuenta.
                val et = Rect(r.left + ancho * 0.35f, r.top + alto * 0.12f, r.right - ancho * 0.04f, r.top + alto * 0.5f)
                drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(1 * u, 1.5f * u), et.size)
                drawRect(cp(PAPEL), et.topLeft, et.size)
                drawRect(c(HIERRO).copy(alpha = 0.7f), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(0.8f * u))
                drawRect(c(HIERRO).copy(alpha = 0.7f), et.deflate(4.6f * u).topLeft, et.deflate(4.6f * u).size, style = Stroke(0.4f * u))
                textoEn(medidor, e.nombre, Rect(et.left + 8 * u, et.top + 5 * u, et.right - 8 * u, et.top + et.height * 0.58f), letra, c(TINTA), 13f, peso = FontWeight.Bold, minimo = 7f)
                textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, et.top + et.height * 0.58f, et.right - 8 * u, et.bottom - 5 * u), letra, c(HELECHO), 9.5f, minimo = 6f)
                if (abre > 0f) drawRect(Color.Black.copy(alpha = 0.4f * abre), r.topLeft, r.size)
            }
            if (abre > 0.01f && sy > 0.02f) drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + alto * abre - 12 * u * sin(PI.toFloat() * abre)), Size(ancho, 12 * u * sin(PI.toFloat() * abre)))
            // La cinta de algodón alrededor, con su lazo: al abrir se deshace.
            val queda = 1f - suelta
            if (queda > 0f) {
                val cy = r.top + alto * 0.66f + suelta * 14 * u
                drawRect(c(CINTA_ALGODON).copy(alpha = queda), Offset(r.left - 2 * u, cy - 2.5f * u), Size(ancho + 4 * u, 5 * u))
                val nudo = Offset(r.left + ancho * 0.72f, cy)
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(nudo.x, nudo.y); quadraticTo(nudo.x + s * 14 * u, nudo.y - 14 * u, nudo.x + s * 16 * u, nudo.y - 2 * u); quadraticTo(nudo.x + s * 10 * u, nudo.y + 2 * u, nudo.x, nudo.y) },
                        c(CINTA_ALGODON).copy(alpha = queda), style = Stroke(2.6f * u))
                    drawLine(c(CINTA_ALGODON).copy(alpha = queda), nudo, nudo + Offset(s * 9 * u, 16 * u), 2.6f * u)
                }
                drawCircle(c(mezcla(CINTA_ALGODON, Color.Black, 0.12f)).copy(alpha = queda), 3 * u, nudo)
            }
        }
    }

    /**
     * La pestaña del día es una etiqueta de herbario de cartulina, de esquinas cortadas arriba, con su ojal y el cordel: la
     * letra de la semana junto al ojal, el día grande y, abajo, una hoja prensada en el color del ánimo (sin ánimo, solo su
     * contorno a lápiz). La hoja se inclina un poco según la semilla.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 4 * u, w - 4 * u, h - 3 * u)
        val s = 7 * u
        val forma = Path().apply { moveTo(r.left + s, r.top); lineTo(r.right - s, r.top); lineTo(r.right, r.top + s); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + s); close() }
        translate(1 * u, 1.4f * u) { drawPath(forma, Color.Black.copy(alpha = 0.22f)) }
        drawPath(forma, c(if ((e.semilla and 1) == 0) PAPEL else mezcla(PAPEL, OTONO, 0.25f)))
        drawPath(forma, c(ROBLE_VETA).copy(alpha = 0.5f), style = Stroke(0.7f * u))
        val ojal = Offset(r.center.x, r.top + 5 * u)
        drawCircle(c(LATON), 2.6f * u, ojal); drawCircle(c(FONDO), 1.2f * u, ojal)
        drawLine(c(CINTA_ALGODON), ojal, Offset(ojal.x + 6 * u, 0f), 1 * u)
        textoEn(medidor, e.semana, Rect(r.left + 2 * u, r.top + 2 * u, r.left + 12 * u, r.top + 11 * u), letra, c(TINTA).copy(alpha = 0.7f), 7.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 2 * u, r.top + 11 * u, r.right - 2 * u, r.top + 31 * u), letra, c(TINTA), 18f, peso = FontWeight.Bold, minimo = 9f)
        drawLine(c(TINTA).copy(alpha = 0.3f), Offset(r.left + 5 * u, r.top + 33 * u), Offset(r.right - 5 * u, r.top + 33 * u), 0.6f * u)
        val base = Offset(r.left + 8 * u, r.bottom - 8 * u)
        val giro = -14f - 10f * azar(e.semilla, 3)
        if (e.tono != null) hojaSimple(base, giro, 22 * u, e.tono, c(TINTA).copy(alpha = 0.35f))
        else rotate(giro, base) {
            drawPath(Path().apply { moveTo(base.x, base.y); quadraticTo(base.x + 11 * u, base.y - 7 * u, base.x + 22 * u, base.y); quadraticTo(base.x + 11 * u, base.y + 7 * u, base.x, base.y); close() },
                c(TINTA).copy(alpha = 0.45f), style = Stroke(0.8f * u))
        }
    }

    /** El mes en una etiqueta larga de herbario: papel crema con doble filete verde, una ramita en cada punta. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(8 * u, 4 * u, w - 8 * u, h - 4 * u)
        drawRect(Color.Black.copy(alpha = 0.18f), b.topLeft + Offset(1 * u, 1.5f * u), b.size)
        drawRect(c(PAPEL), b.topLeft, b.size)
        drawRect(c(HIERRO), b.deflate(2.5f * u).topLeft, b.deflate(2.5f * u).size, style = Stroke(1 * u))
        drawRect(c(HIERRO), b.deflate(4.5f * u).topLeft, b.deflate(4.5f * u).size, style = Stroke(0.5f * u))
        hojaSimple(Offset(b.left + 9 * u, b.center.y + 4 * u), -20f, 20 * u, c(HELECHO), c(TINTA).copy(alpha = 0.3f))
        hojaSimple(Offset(b.left + 12 * u, b.center.y + 4 * u), 20f, 16 * u, c(HOJA_CLARA), c(TINTA).copy(alpha = 0.3f))
        hojaSimple(Offset(b.right - 9 * u, b.center.y + 4 * u), 200f, 20 * u, c(HELECHO), c(TINTA).copy(alpha = 0.3f))
        hojaSimple(Offset(b.right - 12 * u, b.center.y + 4 * u), 160f, 16 * u, c(OTONO), c(TINTA).copy(alpha = 0.3f))
        textoEn(medidor, texto, Rect(b.left + 36 * u, b.top + 3 * u, b.right - 36 * u, b.bottom - 3 * u), letra, c(TINTA), 13f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El lacre verde, la rafia con que se ata y el papel verde del forro. */
    private val LACRE_VERDE = Color(0xFF3F5A2C)
    private val RAFIA = Color(0xFFD8C08A)
    private val FORRO_VERDE = Color(0xFF8FA36A)

    /** El lacre verde: la gota de cera con la hoja estampada en relieve. */
    private fun DrawScope.lacreHoja(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val gota = Path().apply {
            for (i in 0 until 14) {
                val a = (2 * PI * i / 14).toFloat()
                val rr = r * (0.9f + 0.16f * azar(i, 71))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        translate(r * 0.07f, r * 0.1f) { drawPath(gota, Color.Black.copy(alpha = 0.28f * alfa)) }
        drawPath(gota, k(LACRE_VERDE))
        drawCircle(k(mezcla(LACRE_VERDE, Color.Black, 0.3f)), r * 0.74f, c, style = Stroke(r * 0.07f))
        hojaPrensada(Offset(c.x - r * 0.1f, c.y + r * 0.45f), r * 0.95f, r * 0.42f, 18f, k(mezcla(LACRE_VERDE, HOJA_CLARA, 0.55f)), k(mezcla(LACRE_VERDE, Color.Black, 0.25f)))
        drawArc(k(Color.White.copy(alpha = 0.3f)), 200f, 60f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    /** La solapa con orilla de ondas, como las bolsas de semillas cortadas a tijera de picos. */
    private fun solapaOndas(l: Float, r: Float, top: Float, hondo: Float): Path = Path().apply {
        val w = r - l; val cx = (l + r) / 2
        moveTo(l, top); lineTo(r, top); lineTo(r, top + hondo * 0.35f)
        val n = 8
        for (i in 0 until n) {
            val x0 = r - w * i / n; val x1 = r - w * (i + 1) / n
            val y0 = top + hondo * (0.35f + 0.65f * (1f - kotlin.math.abs((x0 - cx) / (w / 2))))
            val y1 = top + hondo * (0.35f + 0.65f * (1f - kotlin.math.abs((x1 - cx) / (w / 2))))
            quadraticTo((x0 + x1) / 2, (y0 + y1) / 2 + hondo * 0.12f, x1, y1)
        }
        close()
    }

    /**
     * El sobre de semillas del naturalista, de papel kraft: la solapa de ondas con la lámina de una campanilla grabada en
     * su marco, la etiqueta de herbario pegada abajo (con su orla y a quién va y las fechas, a pluma) y por dentro el forro
     * de papel verde con hojitas. Lo cierra el lacre verde con la hoja; las de al futuro, además, la rafia en cruz anudada
     * con una ramita de helecho prensado. Al abrirlo, el lacre se parte, la solapa de ondas se levanta y sale la carta con
     * una hoja prensada en la esquina. En modo oscuro, el kraft se hunde en la penumbra del invernadero; la etiqueta, clara.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val rad = CornerRadius(2 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(l + 1.5f * u, top + 2.5f * u), b.size, rad)
        // El forro verde con hojitas.
        drawRoundRect(c(FORRO_VERDE), b.topLeft, b.size, rad)
        for (j in 0 until 10) hojaSimple(Offset(l + W * azar(e.semilla, j + 70), top + H * 0.45f * azar(e.semilla, j + 80)), 360f * azar(e.semilla, j + 90), 5 * u, c(mezcla(FORRO_VERDE, HELECHO, 0.5f)), c(FORRO_VERDE))
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val hondo = H * 0.42f
        val solapa = solapaOndas(l, r, top, hondo)
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            drawPath(solapa, c(mezcla(KRAFT, FORRO_VERDE, 0.5f)))
            drawPath(solapa, c(ROBLE_VETA).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        }
        // La carta, con su hoja prensada.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.82f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 8 * u, 5.5f * u, 7 * u, oscuro)
            hojaPrensada(Offset(h.right - 9 * u, h.top + 16 * u), 12 * u, 4 * u, 30f, c(HOJA_CLARA).copy(alpha = 0.8f), c(HELECHO))
        }
        // El frente de kraft.
        val v = top + H * 0.3f
        val frente = Path().apply {
            moveTo(l, top + H * 0.1f); quadraticTo(cx, v + H * 0.06f, r, top + H * 0.1f); lineTo(r, bot - 2 * u)
            quadraticTo(r, bot, r - 2 * u, bot); lineTo(l + 2 * u, bot); quadraticTo(l, bot, l, bot - 2 * u); close()
        }
        drawPath(frente, c(KRAFT))
        clipPath(frente) {
            for (j in 0 until 40) { val p = Offset(l + W * azar(e.semilla, j + 100), top + H * azar(e.semilla, j + 140)); drawLine(c(ROBLE_VETA).copy(alpha = 0.18f), p, p + Offset(3 * u, 0.6f * u), 0.6f * u) }
            drawLine(c(ROBLE_VETA).copy(alpha = 0.4f), Offset(l, bot), Offset(cx - W * 0.1f, v + H * 0.1f), 0.8f * u)
            drawLine(c(ROBLE_VETA).copy(alpha = 0.4f), Offset(r, bot), Offset(cx + W * 0.1f, v + H * 0.1f), 0.8f * u)
        }
        drawPath(Path().apply { moveTo(l, top + H * 0.1f); quadraticTo(cx, v + H * 0.06f, r, top + H * 0.1f) }, c(ROBLE_VETA).copy(alpha = 0.6f), style = Stroke(1 * u))
        // La etiqueta de herbario.
        val et = Rect(l + W * 0.07f, top + H * 0.58f, r - W * 0.2f, top + H * 0.93f)
        drawRect(Color.Black.copy(alpha = 0.18f), et.topLeft + Offset(0.8f * u, 1 * u), et.size)
        drawRect(if (oscuro) mezcla(PAPEL, Color.Black, 0.12f) else PAPEL, et.topLeft, et.size)
        drawRect(c(HELECHO), et.deflate(1.5f * u).topLeft, et.deflate(1.5f * u).size, style = Stroke(0.8f * u))
        drawRect(c(HELECHO), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(0.4f * u))
        textoEn(medidor, e.linea1, Rect(et.left + 4 * u, et.top + 3 * u, et.right - 4 * u, et.top + et.height * 0.58f), letra, TINTA, 11.5f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(et.left + 4 * u, et.top + et.height * 0.55f, et.right - 4 * u, et.bottom - 3 * u), LetraExtra.fraunces, mezcla(TINTA, BEGONIA, 0.4f), 9f, minimo = 6f)
        // La solapa de ondas, cerrada, con la lámina de la campanilla.
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            translate(0f, 1.5f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.14f)) }
            drawPath(solapa, c(mezcla(KRAFT, PAPEL, 0.12f)))
            clipPath(solapa) {
                val lam = Rect(l + W * 0.08f, top + H * 0.06f, l + W * 0.08f + H * 0.3f, top + H * 0.34f)
                drawRect(c(PAPEL), lam.topLeft, lam.size)
                drawRect(c(ROBLE_VETA), lam.deflate(1.2f * u).topLeft, lam.deflate(1.2f * u).size, style = Stroke(0.5f * u))
                val base = Offset(lam.center.x, lam.bottom - 2 * u)
                drawPath(Path().apply { moveTo(base.x, base.y); quadraticTo(base.x - lam.width * 0.1f, lam.center.y, base.x + lam.width * 0.15f, lam.top + lam.height * 0.25f) }, c(HELECHO), style = Stroke(0.7f * u))
                hojaSimple(Offset(base.x - 0.5f * u, base.y - lam.height * 0.25f), 200f, lam.width * 0.3f, c(HOJA_CLARA), c(HELECHO))
                listOf(Offset(lam.width * 0.15f, lam.height * 0.25f), Offset(-lam.width * 0.12f, lam.height * 0.45f)).forEach { o ->
                    val f = Offset(base.x, lam.top) + o
                    drawPath(Path().apply { moveTo(f.x - lam.width * 0.1f, f.y); lineTo(f.x + lam.width * 0.1f, f.y); lineTo(f.x + lam.width * 0.13f, f.y + lam.height * 0.16f)
                        lineTo(f.x, f.y + lam.height * 0.12f); lineTo(f.x - lam.width * 0.13f, f.y + lam.height * 0.16f); close() }, c(Color(0xFF6E6AA8)))
                }
                textoEn(medidor, "SEMILLAS", Rect(lam.right + 4 * u, top + H * 0.08f, r - W * 0.06f, top + H * 0.2f), letra, c(TINTA), 9f, peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 4f)
                drawLine(c(TINTA).copy(alpha = 0.4f), Offset(lam.right + 8 * u, top + H * 0.22f), Offset(r - W * 0.1f, top + H * 0.22f), 0.5f * u)
            }
            drawPath(solapa, c(ROBLE_VETA).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        }
        // La rafia en cruz, con su moño y una ramita de helecho.
        val y0 = top + hondo
        val xr = r - W * 0.12f
        if (e.cordon > 0f) {
            val h = Path().apply { moveTo(l - 2 * u, y0 + 0.5f * u); lineTo(r + 2 * u, y0 - 0.5f * u) }
            val vt = Path().apply { moveTo(xr, top - 2 * u); lineTo(xr + 1 * u, bot + 2 * u) }
            listOf(h, vt).forEachIndexed { i, p ->
                val f = ((e.cordon - i * 0.4f) / 0.5f).coerceIn(0f, 1f)
                translate(0.6f * u, 1 * u) { trazoParcialCo(p, f, Color.Black.copy(alpha = 0.18f), 3 * u, StrokeCap.Butt) }
                trazoParcialCo(p, f, c(RAFIA), 3 * u, StrokeCap.Butt)
                trazoParcialCo(p, f, c(mezcla(RAFIA, ROBLE_VETA, 0.3f)), 0.6f * u)
            }
            val nudo = ((e.cordon - 0.85f) / 0.15f).coerceIn(0f, 1f)
            if (nudo > 0f) scale(nudo, nudo, Offset(xr, y0)) {
                frondaPrensada(Offset(xr - 2 * u, y0 + 6 * u), 22 * u, -40f, c(HELECHO))
                drawOval(c(RAFIA), Offset(xr - 9 * u, y0 - 4 * u), Size(9 * u, 7 * u), style = Stroke(2 * u))
                drawOval(c(RAFIA), Offset(xr, y0 - 4 * u), Size(9 * u, 7 * u), style = Stroke(2 * u))
                drawCircle(c(mezcla(RAFIA, ROBLE_VETA, 0.2f)), 2.4f * u, Offset(xr, y0))
            }
        }
        // El lacre verde.
        val selloR = H * 0.13f
        val selloC = Offset(cx, top + hondo * k)
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(LUCIERNAGA), e.brillo)
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, selloC.y - y0 - selloR * 0.3f * e.romper),
                Offset(selloR * 0.25f * e.romper, selloR * 0.3f * e.romper), e.semilla) { lacreHoja(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.25f, selloR * 0.3f), e.semilla, conArriba = false) { lacreHoja(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        }
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.24f, 0.8f, 0.39f)

    override val altoBuzon: Dp = 208.dp

    /** Un sobre de semillas de pie en su bolsillo: listo (le brota una flor), atado con rafia o abierto con la carta. */
    private fun DrawScope.sobreSemillas(r: Rect, tipo: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        if (tipo == 0) {
            // Le brota una flor: el tallo, dos hojas y la corola abierta.
            val b = Offset(r.center.x, r.top + 2 * u)
            drawLine(c(HELECHO), b, b - Offset(0f, r.height * 0.55f), 1 * u)
            hojaSimple(b - Offset(0f, r.height * 0.2f), -150f, r.width * 0.3f, c(HOJA_CLARA), c(HELECHO))
            hojaSimple(b - Offset(0f, r.height * 0.3f), -30f, r.width * 0.3f, c(HOJA_CLARA), c(HELECHO))
            val f = b - Offset(0f, r.height * 0.58f)
            for (i in 0 until 5) { val a = (2 * PI * i / 5).toFloat(); drawCircle(c(listOf(Color(0xFFD9785A), Color(0xFF8C7AC8), Color(0xFFE2B84A))[semilla % 3]), r.width * 0.11f, Offset(f.x + cos(a) * r.width * 0.1f, f.y + sin(a) * r.width * 0.1f)) }
            drawCircle(c(Color(0xFFF2D86A)), r.width * 0.07f, f)
        }
        if (tipo == 2) {
            drawRect(c(PAPEL), Offset(r.left + r.width * 0.1f, r.top - r.height * 0.35f), Size(r.width * 0.8f, r.height * 0.6f))
            for (j in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, r.top - r.height * (0.25f - j * 0.1f)), Offset(r.right - r.width * 0.2f, r.top - r.height * (0.25f - j * 0.1f)), 0.6f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.22f), r.topLeft + Offset(1 * u, 1 * u), r.size)
        drawRect(c(KRAFT), r.topLeft, r.size)
        val lam = Rect(r.left + r.width * 0.14f, r.top + r.height * 0.3f, r.right - r.width * 0.14f, r.top + r.height * 0.78f)
        drawRect(c(PAPEL), lam.topLeft, lam.size)
        val col = listOf(Color(0xFFB0473A), Color(0xFF6E6AA8), Color(0xFFD9A43A), Color(0xFF4F7D3E))[semilla % 4]
        drawCircle(c(col), lam.width * 0.2f, Offset(lam.center.x, lam.top + lam.height * 0.4f))
        drawLine(c(HELECHO), Offset(lam.center.x, lam.top + lam.height * 0.55f), Offset(lam.center.x, lam.bottom - 1.5f * u), 0.8f * u)
        if (tipo == 2) drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, r.top - r.height * 0.18f); lineTo(r.right, r.top) }, c(mezcla(KRAFT, FORRO_VERDE, 0.4f)))
        else drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.12f); lineTo(r.left, r.top + r.height * 0.12f); close() }, c(mezcla(KRAFT, ROBLE_VETA, 0.2f)))
        if (tipo == 1) {
            drawLine(c(RAFIA), Offset(r.left - 1 * u, r.top + r.height * 0.2f), Offset(r.right + 1 * u, r.top + r.height * 0.2f), 2 * u)
            drawLine(c(RAFIA), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 2 * u)
        }
        if (tipo != 2) drawCircle(c(LACRE_VERDE), r.width * 0.12f, Offset(r.center.x, r.top + r.height * 0.18f))
    }

    /**
     * El expositor de semillas del vivero, contra la pared de tablas pintadas del invernadero: un mueble inclinado de roble
     * de dos gradas con cinco bolsillos cada una, y en cada bolsillo un sobre de semillas por carta (hasta diez). A los que
     * ya se pueden abrir les brota una flor y los alumbra un resplandor (el primer degradado); los sellados van atados con
     * rafia y su lacre verde; los abiertos asoman la carta. Arriba, la tabla pintada «SEMILLAS» entre dos ramas; abajo, tres
     * etiquetas de herbario clavadas en el canto con la cuenta de cada clase, y macetas de barro con plantones a los pies. En
     * modo oscuro, el invernadero de noche: el farol de campo encendido (el segundo degradado) y unas luciérnagas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF070A08), 0.4f) else t(x, false, false)
        // La pared de tablas.
        drawRect(m(PARED))
        var x = 0f; while (x < w) { drawLine(m(mezcla(PARED, ROBLE_VETA, 0.35f)), Offset(x, 0f), Offset(x, h), 1 * u); x += 18 * u }
        if (oscuro) mancha(Offset(w * 0.92f, h * 0.2f), w * 0.5f, FAROL_CAMPO.copy(alpha = 0.25f))
        // El farol de campo colgado.
        val fa = Offset(w * 0.92f, 10 * u)
        drawLine(m(HIERRO), Offset(fa.x, 0f), fa, 1 * u)
        drawRoundRect(c(LATA), Offset(fa.x - 6 * u, fa.y), Size(12 * u, 3 * u), CornerRadius(1 * u))
        drawRect(if (oscuro) FAROL_CAMPO else c(VIDRIO), Offset(fa.x - 4.5f * u, fa.y + 3 * u), Size(9 * u, 12 * u))
        drawRect(c(LATA), Offset(fa.x - 4.5f * u, fa.y + 3 * u), Size(9 * u, 12 * u), style = Stroke(1 * u))
        drawRoundRect(c(LATA), Offset(fa.x - 6 * u, fa.y + 15 * u), Size(12 * u, 3 * u), CornerRadius(1 * u))
        // La tabla pintada.
        val tabla = Rect(w * 0.3f, 6 * u, w * 0.7f, 26 * u)
        drawRect(Color.Black.copy(alpha = 0.2f), tabla.topLeft + Offset(1 * u, 1.5f * u), tabla.size)
        drawRect(m(HIERRO), tabla.topLeft, tabla.size)
        textoEn(medidor, "SEMILLAS", tabla.deflate(3 * u), letra, c(PAPEL), 12f, peso = FontWeight.SemiBold, espaciado = 2f, minimo = 6f)
        fronda(Offset(tabla.left - 2 * u, tabla.bottom), 22 * u, -60f, c(HELECHO))
        fronda(Offset(tabla.right + 2 * u, tabla.bottom), 22 * u, 60f, c(HELECHO))
        // El expositor de dos gradas.
        val mue = Rect(w * 0.08f, 44 * u, w * 0.92f, h - 42 * u)
        val cartas = cartasBuzonCo(e, 10)
        val gh = mue.height / 2
        for (g in 0 until 2) {
            val gr = Rect(mue.left + g * 6 * u, mue.top + g * gh, mue.right - g * 6 * u, mue.top + (g + 1) * gh)
            drawRect(c(ROBLE_VETA), Offset(gr.left, gr.top + gh * 0.35f), Size(gr.width, gh * 0.65f))
            val pw = gr.width / 5
            for (k in 0 until 5) {
                val i = g * 5 + k
                val tipo = cartas.getOrNull(i) ?: continue
                val sr = Rect(gr.left + k * pw + pw * 0.2f, gr.top + gh * 0.18f, gr.left + (k + 1) * pw - pw * 0.2f, gr.bottom - gh * 0.2f)
                if (tipo == 0) mancha(Offset(sr.center.x, sr.top), pw * 0.7f, c(LUCIERNAGA).copy(alpha = 0.5f))
                sobreSemillas(sr, tipo, oscuro, i * 5 + 1)
            }
            // El frente del bolsillo: una tabla de roble con su canto.
            val fr = Rect(gr.left, gr.bottom - gh * 0.42f, gr.right, gr.bottom)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(fr.left, fr.top - 2 * u), Size(fr.width, 2 * u))
            drawRect(c(ROBLE), fr.topLeft, fr.size)
            vetas(fr, c(ROBLE_VETA).copy(alpha = 0.5f), 121 + g, densidad = 0.8f)
            drawRect(c(ROBLE_LUZ), fr.topLeft, Size(fr.width, 1.5f * u))
            for (k in 1 until 5) drawLine(c(ROBLE_VETA), Offset(gr.left + k * pw, fr.top), Offset(gr.left + k * pw, fr.bottom), 1 * u)
        }
        // Las patas y las tres etiquetas de herbario con la cuenta.
        val pie = Rect(mue.left, mue.bottom, mue.right, h - 8 * u)
        listOf(pie.left + 6 * u, pie.right - 10 * u).forEach { px -> drawRect(c(ROBLE_VETA), Offset(px, pie.top), Size(4 * u, pie.height)) }
        drawRect(c(ROBLE), Offset(pie.left, pie.top), Size(pie.width, 4 * u))
        val cuentas = listOf("POR ABRIR" to e.listas, "SELLADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val ew = minOf(pie.width * 0.29f, 104 * u)
        val gap = (pie.width - ew * 3) / 4
        cuentas.forEachIndexed { i, (txt, n) ->
            val et = Rect(pie.left + gap + i * (ew + gap), pie.top + 6 * u, pie.left + gap + i * (ew + gap) + ew, pie.bottom - 2 * u)
            drawLine(c(LATON), Offset(et.center.x, pie.top), Offset(et.center.x, et.top), 0.8f * u)
            drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(0.8f * u, 1 * u), et.size)
            drawRect(if (oscuro) mezcla(PAPEL, Color.Black, 0.12f) else PAPEL, et.topLeft, et.size)
            drawRect(c(HELECHO), et.deflate(1.5f * u).topLeft, et.deflate(1.5f * u).size, style = Stroke(0.6f * u))
            textoEn(medidor, txt, Rect(et.left + 3 * u, et.top + 2 * u, et.left + et.width * 0.64f, et.bottom - 2 * u), letra, TINTA, 7.5f, peso = FontWeight.SemiBold, minimo = 5f)
            textoEn(medidor, "$n", Rect(et.left + et.width * 0.66f, et.top + 1 * u, et.right - 3 * u, et.bottom - 1 * u), LetraExtra.fraunces, if (i == 0 && n > 0) BEGONIA else TINTA, 14f, peso = FontWeight.Bold, minimo = 6f)
        }
        // Las macetas con plantones, a los pies.
        listOf(0.03f, 0.97f).forEachIndexed { i, f ->
            val mx = w * f
            val ma = Path().apply { moveTo(mx - 9 * u, h - 22 * u); lineTo(mx + 9 * u, h - 22 * u); lineTo(mx + 6.5f * u, h); lineTo(mx - 6.5f * u, h); close() }
            drawPath(ma, c(BARRO))
            drawRect(c(mezcla(BARRO, Color.Black, 0.2f)), Offset(mx - 10 * u, h - 24 * u), Size(20 * u, 4 * u))
            fronda(Offset(mx, h - 24 * u), 24 * u, if (i == 0) 12f else -12f, c(HELECHO))
            hojaSimple(Offset(mx, h - 26 * u), -120f, 10 * u, c(HOJA_CLARA), c(HELECHO))
        }
        if (oscuro) listOf(Offset(w * 0.2f, h * 0.12f), Offset(w * 0.76f, h * 0.3f), Offset(w * 0.5f, h * 0.06f), Offset(w * 0.12f, h * 0.55f)).forEach { luciernaga(it, 1.4f * u) }
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
 * La tapa es ese libro de tela atado con cinta de raso y moño, con la etiqueta de regalo donde va la fecha y la cuenta
 * hasta Navidad, y un cordón de luces encima que de noche se enciende; al abrirlo, la cinta se desata.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.27f, 0.44f, 0.75f, 0.74f)
    override val letraTapa: FontFamily = LetraExtra.italianno
    override val tamTapa: Float = 18f
    override val lineasTapa: Int = 2
    override val centradoTapa: Boolean = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La cartulina de la etiqueta de regalo: hundida en oscuro y en la penumbra de noche, pero clara para el extracto. */
    private fun cremaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = CREMA
        if (oscuro) r = mezcla(r, Color(0xFF100C0A), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.13f)
        return r
    }

    /** La silueta de una etiqueta de regalo: rectángulo con la punta de la izquierda cortada en chaflán. */
    private fun etiquetaRegalo(r: Rect, k: Float): Path = Path().apply {
        moveTo(r.left + k, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left + k, r.bottom)
        lineTo(r.left, r.bottom - k); lineTo(r.left, r.top + k); close()
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val it0 = (e.semilla and 0x7fffffff) % TELAS.size
        val tela = c(TELAS[it0])
        val roja = it0 == 0 || it0 == 2
        val oro = c(ORO)
        val cinta = c(if (roja) ORO else ROJO)
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val cu = Rect(w * 0.02f, h * 0.1f, w * 0.965f, h * 0.935f)
        val bisagra = cu.left + w * 0.07f
        drawRoundRect(Color.Black.copy(alpha = 0.2f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(3 * u))
        // La cinta de registro, que cuelga por el pie: más larga cuanto más escribiste.
        val xc = w * 0.64f; val cw = w * 0.022f; val lc = cu.bottom + h * (0.025f + 0.035f * lleno)
        drawPath(Path().apply { moveTo(xc, cu.bottom); lineTo(xc + cw, cu.bottom); lineTo(xc + cw, lc); lineTo(xc + cw / 2, lc - cw * 0.6f); lineTo(xc, lc); close() }, cinta)
        // El canto de las hojas.
        drawRect(c(CREMA), Offset(cu.left + 3 * u, cu.top + 3 * u), cu.size)
        for (k in 1..3) drawLine(c(NOGAL).copy(alpha = 0.3f), Offset(cu.left + 8 * u, cu.bottom + k * 0.7f * u), Offset(cu.right + 2 * u, cu.bottom + k * 0.7f * u), 0.4f * u)
        // Por dentro (se ve al abrirse): la hoja con sus renglones, su margen y una ramita de acebo dibujada en la esquina.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(CREMA), hoja.topLeft, hoja.size)
            var ly = hoja.top + 20 * u
            while (ly < hoja.bottom - 4 * u) { drawLine(c(ROJO).copy(alpha = 0.18f), Offset(hoja.left + 6 * u, ly), Offset(hoja.right - 6 * u, ly), 0.5f * u); ly += 7 * u }
            drawLine(c(ROJO).copy(alpha = 0.35f), Offset(hoja.left + 22 * u, hoja.top), Offset(hoja.left + 22 * u, hoja.bottom), 0.7f * u)
            acebo(Offset(hoja.right - hoja.width * 0.12f, hoja.top + hoja.height * 0.18f), h * 0.12f, c(VERDE), c(ROJO))
        }
        // La tapa de tela, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRoundRect(tela, cu.topLeft, cu.size, CornerRadius(3 * u))
            var yy = cu.top
            while (yy < cu.bottom) { drawLine(Color.White.copy(alpha = 0.05f), Offset(cu.left, yy), Offset(cu.right, yy), 0.5f * u); yy += 2.2f * u }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cu.right - 4 * u, cu.top), Size(4 * u, cu.height))
            // El doble filete dorado, con un botón en cada esquina.
            val marco = Rect(bisagra + 7 * u, cu.top + 7 * u, cu.right - 7 * u, cu.bottom - 7 * u)
            drawRect(oro, marco.topLeft, marco.size, style = Stroke(1 * u))
            drawRect(oro.copy(alpha = 0.7f), marco.deflate(3 * u).topLeft, marco.deflate(3 * u).size, style = Stroke(0.5f * u))
            listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { o ->
                drawCircle(oro, 2.6f * u, o)
                drawCircle(tela, 1.2f * u, o)
            }
            // Abajo a la izquierda, la ramita de acebo; de noche, una estrella con sus destellos. Encima, un copo de nieve.
            val zona = (bisagra + w * 0.2f) / 2 + 1 * u
            val ca = Offset(zona, h * 0.76f)
            val sa = minOf(h * 0.13f, (w * 0.2f - bisagra) * 0.9f)
            if (e.noche) {
                drawPath(estrella(ca, sa * 0.42f, 5, 0.45f), oro)
                listOf(0f, 90f, 180f, 270f).forEach { g ->
                    val r0 = Math.toRadians(g.toDouble() + 45.0).toFloat()
                    drawLine(oro.copy(alpha = 0.7f), Offset(ca.x + sa * 0.5f * cos(r0), ca.y + sa * 0.5f * sin(r0)), Offset(ca.x + sa * 0.7f * cos(r0), ca.y + sa * 0.7f * sin(r0)), 0.6f * u)
                }
            } else acebo(ca, sa, c(mezcla(VERDE, Color.White, 0.15f)), c(ROJO))
            val cn = Offset(zona, h * 0.47f)
            val rn = sa * 0.32f
            for (k in 0 until 6) {
                val g = Math.toRadians(k * 60.0).toFloat()
                val p = Offset(cn.x + rn * cos(g), cn.y + rn * sin(g))
                drawLine(c(NIEVE).copy(alpha = 0.75f), cn, p, 0.7f * u)
                val m = Offset(cn.x + rn * 0.6f * cos(g), cn.y + rn * 0.6f * sin(g))
                listOf(0.6f, -0.6f).forEach { d -> drawLine(c(NIEVE).copy(alpha = 0.6f), m, Offset(m.x + rn * 0.3f * cos(g + d), m.y + rn * 0.3f * sin(g + d)), 0.5f * u) }
            }
            // La etiqueta de regalo, con su ojal: la fecha arriba, el extracto en medio y las palabras al pie.
            val tg = Rect(w * 0.2f, h * 0.3f, w * 0.79f, h * 0.865f)
            val kt = 13 * u
            drawPath(etiquetaRegalo(Rect(tg.left + 1.2f * u, tg.top + 1.8f * u, tg.right + 1.2f * u, tg.bottom + 1.8f * u), kt), Color.Black.copy(alpha = 0.3f))
            drawPath(etiquetaRegalo(tg, kt), cremaTapa(oscuro, e.noche))
            drawPath(etiquetaRegalo(tg, kt), c(ROJO).copy(alpha = 0.75f), style = Stroke(1 * u))
            drawPath(etiquetaRegalo(tg.deflate(3.5f * u), kt * 0.8f), c(ROJO).copy(alpha = 0.35f), style = Stroke(0.6f * u))
            val ojal = Offset(tg.left + 9 * u, tg.center.y)
            drawCircle(tela, 2.6f * u, ojal)
            drawCircle(oro, 3.4f * u, ojal, style = Stroke(1.3f * u))
            // La fecha, en uno o dos renglones (la cuenta hasta Navidad va en el segundo).
            val (f1, f2) = partirFecha(e.fecha, 26)
            if (f2 == null) {
                textoEn(medidor, f1, Rect(w * 0.27f, h * 0.325f, w * 0.76f, h * 0.41f), letra, c(ROJO), 11f, peso = FontWeight.SemiBold, minimo = 6f)
            } else {
                textoEn(medidor, f1, Rect(w * 0.27f, h * 0.318f, w * 0.76f, h * 0.372f), letra, c(ROJO), 10f, peso = FontWeight.SemiBold, minimo = 6f)
                textoEn(medidor, f2, Rect(w * 0.27f, h * 0.372f, w * 0.76f, h * 0.42f), letra, c(ROJO).copy(alpha = 0.85f), 8.5f, minimo = 6f)
            }
            drawLine(c(ROJO).copy(alpha = 0.5f), Offset(w * 0.4f, h * 0.428f), Offset(w * 0.62f, h * 0.428f), 0.6f * u)
            drawLine(c(ROJO).copy(alpha = 0.5f), Offset(w * 0.4f, h * 0.75f), Offset(w * 0.62f, h * 0.75f), 0.6f * u)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.27f, h * 0.758f, w * 0.76f, h * 0.835f), letra, c(TINTA).copy(alpha = 0.7f), 9.5f, minimo = 6f)
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(3 * u))
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo, más oscuro, con sus filetes dorados.
        drawRect(tela, cu.topLeft, Size(bisagra - cu.left, cu.height))
        drawRect(Color.Black.copy(alpha = 0.28f), cu.topLeft, Size(bisagra - cu.left, cu.height))
        listOf(0.1f, 0.9f).forEach { f ->
            val y = cu.top + cu.height * f
            drawLine(oro, Offset(cu.left + 2 * u, y), Offset(bisagra - 2 * u, y), 0.6f * u)
            drawLine(oro, Offset(cu.left + 2 * u, y + 1.8f * u), Offset(bisagra - 2 * u, y + 1.8f * u), 0.5f * u)
        }
        drawPath(estrella(Offset((cu.left + bisagra) / 2, cu.center.y), 3 * u, 5, 0.45f), oro)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 1 * u)
        // La cinta de raso que lo envuelve, con su moño y el cordel de la etiqueta: al abrirse, se desata.
        val atado = (1f - a * 2.2f).coerceAtLeast(0f)
        if (atado > 0f) {
            val y0 = h * 0.235f; val xv = w * 0.86f; val m = 4.5f * u
            val raso = cinta.copy(alpha = atado)
            drawRect(Color.Black.copy(alpha = 0.18f * atado), Offset(cu.left, y0 - m + 1.5f * u), Size(cu.width, m * 2))
            drawRect(raso, Offset(cu.left - 1 * u, y0 - m), Size(cu.width + 2 * u, m * 2))
            drawRect(Color.Black.copy(alpha = 0.18f * atado), Offset(xv - m + 1.5f * u, cu.top), Size(m * 2, cu.height))
            drawRect(raso, Offset(xv - m, cu.top - 1 * u), Size(m * 2, cu.height + 2 * u))
            drawLine(Color.White.copy(alpha = 0.3f * atado), Offset(cu.left, y0 - m * 0.3f), Offset(cu.right, y0 - m * 0.3f), 0.8f * u)
            drawLine(Color.White.copy(alpha = 0.3f * atado), Offset(xv - m * 0.3f, cu.top), Offset(xv - m * 0.3f, cu.bottom), 0.8f * u)
            val nudo = Offset(xv, y0)
            // El cordel que va del moño al ojal de la etiqueta, por encima y por fuera de ella.
            drawPath(Path().apply {
                moveTo(nudo.x, nudo.y); cubicTo(w * 0.6f, h * 0.29f, w * 0.17f, h * 0.25f, w * 0.165f, h * 0.4f)
                quadraticTo(w * 0.16f, h * 0.55f, w * 0.2f + 9 * u, h * 0.5825f)
            }, c(CORDEL).copy(alpha = atado), style = Stroke(0.9f * u))
            // Las dos caídas del moño, con su corte en V.
            listOf(-1f, 1f).forEach { s ->
                val fin = Offset(nudo.x + s * 12 * u, nudo.y + 22 * u)
                drawPath(Path().apply {
                    moveTo(nudo.x - 2.5f * u, nudo.y); lineTo(nudo.x + 2.5f * u, nudo.y); lineTo(fin.x + 2.5f * u, fin.y); lineTo(fin.x, fin.y - 3 * u); lineTo(fin.x - 2.5f * u, fin.y); close()
                }, raso)
            }
            // Las dos lazadas.
            listOf(-1f, 1f).forEach { s ->
                val lazada = Path().apply {
                    moveTo(nudo.x, nudo.y)
                    cubicTo(nudo.x + s * 16 * u, nudo.y - 15 * u, nudo.x + s * 24 * u, nudo.y + 2 * u, nudo.x, nudo.y)
                }
                drawPath(lazada, raso)
                drawPath(lazada, Color.Black.copy(alpha = 0.25f * atado), style = Stroke(0.7f * u))
                drawLine(Color.White.copy(alpha = 0.3f * atado), Offset(nudo.x + s * 6 * u, nudo.y - 5 * u), Offset(nudo.x + s * 13 * u, nudo.y - 6 * u), 0.8f * u)
            }
            drawRoundRect(raso, Offset(nudo.x - 3.5f * u, nudo.y - 4 * u), Size(7 * u, 8 * u), CornerRadius(2 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.25f * atado), Offset(nudo.x - 3.5f * u, nudo.y - 4 * u), Size(7 * u, 8 * u), CornerRadius(2 * u), style = Stroke(0.6f * u))
        }
        // El cordón de luces que cuelga por encima en tres festones: de noche, encendidas.
        val cuerda = c(ABETO_OSC)
        var j = 0
        for (f in 0 until 3) {
            val p0 = Offset(w * f / 3f, h * 0.03f); val p2 = Offset(w * (f + 1) / 3f, h * 0.03f)
            val cc = Offset((p0.x + p2.x) / 2, h * 0.15f)
            drawPath(Path().apply { moveTo(p0.x, p0.y); quadraticTo(cc.x, cc.y, p2.x, p2.y) }, cuerda, style = Stroke(1.2f * u))
            listOf(0.25f, 0.5f, 0.75f).forEach { s ->
                val q = 1 - s
                val p = Offset(q * q * p0.x + 2 * q * s * cc.x + s * s * p2.x, q * q * p0.y + 2 * q * s * cc.y + s * s * p2.y)
                val col = BOMBILLAS[(j + (e.semilla and 7)) % BOMBILLAS.size]
                val bulbo = Offset(p.x, p.y + 5 * u)
                if (e.noche) {
                    drawCircle(col.copy(alpha = 0.12f), 9 * u, bulbo)
                    drawCircle(col.copy(alpha = 0.25f), 5 * u, bulbo)
                }
                drawRect(cuerda, Offset(p.x - 1.4f * u, p.y), Size(2.8f * u, 2.6f * u))
                drawOval(if (e.noche) col else c(col).copy(alpha = 0.85f), Offset(bulbo.x - 2.4f * u, bulbo.y - 2.6f * u), Size(4.8f * u, 6.4f * u))
                drawCircle(Color.White.copy(alpha = if (e.noche) 0.7f else 0.35f), 0.9f * u, Offset(bulbo.x - 0.8f * u, bulbo.y - 0.6f * u))
                j++
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 130.dp

    /**
     * Una guirnalda de abeto colgada de [a] a [b] que cae [cae] en el medio: agujas cortas a lo largo de la curva y bombillas
     * de colores cada tanto; [encendida], con su halo.
     */
    private fun DrawScope.guirnalda(a: Offset, b: Offset, cae: Float, bombillas: Int, encendida: Boolean, verde: Color, verdeOsc: Color, semilla: Int, tono: (Color) -> Color) {
        val u = density
        val ctrl = Offset((a.x + b.x) / 2, (a.y + b.y) / 2 + cae * 2)
        fun punto(t0: Float) = Offset((1 - t0) * (1 - t0) * a.x + 2 * (1 - t0) * t0 * ctrl.x + t0 * t0 * b.x, (1 - t0) * (1 - t0) * a.y + 2 * (1 - t0) * t0 * ctrl.y + t0 * t0 * b.y)
        val pasos = ((b.x - a.x) / (2.2f * u)).toInt().coerceIn(8, 400)
        for (k in 0..pasos) {
            val q = punto(k / pasos.toFloat())
            for (j in 0 until 3) {
                val ang = 6.28f * azar(semilla, k * 3 + j)
                val l = (2.5f + 2.5f * azar(semilla, k * 3 + j + 900)) * u
                drawLine(if (j == 0) verdeOsc else verde, q, Offset(q.x + l * cos(ang), q.y + l * sin(ang) * 0.8f), 0.9f * u)
            }
        }
        for (k in 0 until bombillas) {
            val q = punto((k + 0.5f) / bombillas) + Offset(0f, 3 * u)
            val col = tono(BOMBILLAS[(k + semilla) % BOMBILLAS.size])
            if (encendida) { drawCircle(col.copy(alpha = 0.12f), 7 * u, q); drawCircle(col.copy(alpha = 0.25f), 4 * u, q) }
            drawOval(col, Offset(q.x - 1.6f * u, q.y - 1.2f * u), Size(3.2f * u, 4.4f * u))
        }
    }

    /**
     * La cabecera de la carta de Navidad: la guirnalda de abeto en dos festones con su cordón de luces, que se juntan al
     * centro en una ramita de acebo (de noche, la estrella, y las luces encendidas); «— Junto al fuego —» en versales, la
     * fecha en cursiva de pluma, grande, y el detalle; al pie, un filete rojo y uno verde con una estrellita de oro al centro.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val cx = w / 2
        val encendida = e.noche || oscuro
        guirnalda(Offset(0f, 5 * u), Offset(cx, 9 * u), 6 * u, 4, encendida, c(ABETO), c(ABETO_OSC), 3) { c(it) }
        guirnalda(Offset(cx, 9 * u), Offset(w, 5 * u), 6 * u, 4, encendida, c(ABETO), c(ABETO_OSC), 7) { c(it) }
        if (e.noche) {
            val es = Offset(cx, 12 * u)
            drawCircle(c(LLAMA).copy(alpha = 0.15f), 13 * u, es)
            drawPath(estrella(es, 10 * u, 8, 0.32f), c(ORO))
            drawPath(estrella(es, 5 * u, 4, 0.4f, giro = -45f), mezcla(c(ORO), Color.White, 0.5f))
        } else {
            acebo(Offset(cx, 12 * u), 22 * u, c(VERDE), c(ROJO))
        }
        textoEn(medidor, e.rotulo, Rect(6 * u, 30 * u, w - 6 * u, 46 * u), LetraExtra.cinzelDeco, p.acento, 12.5f, espaciado = 1.2f, minimo = 7f)
        fechaCon(medidor, e.fecha, Rect(6 * u, 47 * u, w - 6 * u, 99 * u), LetraExtra.italianno, p.tinta, 34f)
        textoEn(medidor, e.detalle, Rect(6 * u, 100 * u, w - 6 * u, 114 * u), LetraExtra.cinzelDeco, p.tenue, 10f, minimo = 6.5f)
        drawLine(p.acento.copy(alpha = 0.7f), Offset(10 * u, 120 * u), Offset(cx - 10 * u, 120 * u), 1.2f * u)
        drawLine(p.detalle.copy(alpha = 0.7f), Offset(10 * u, 123.5f * u), Offset(cx - 10 * u, 123.5f * u), 0.8f * u)
        drawLine(p.acento.copy(alpha = 0.7f), Offset(cx + 10 * u, 120 * u), Offset(w - 10 * u, 120 * u), 1.2f * u)
        drawLine(p.detalle.copy(alpha = 0.7f), Offset(cx + 10 * u, 123.5f * u), Offset(w - 10 * u, 123.5f * u), 0.8f * u)
        drawPath(estrella(Offset(cx, 121.8f * u), 4.5f * u, 5, 0.45f), c(ORO))
    }

    /**
     * El papel de cartas de Navidad: el doble marco rojo y verde con una ramita de acebo en cada esquina, un cordón de
     * bombillas en festones arriba, copos de nieve apenas dibujados y, al pie, una hilera de abetitos. En modo oscuro, la
     * lumbre de la chimenea alumbra la carta desde abajo a la izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        drawRect(p.acento.copy(alpha = 0.55f), Offset(8 * u, 8 * u), Size(w - 16 * u, h - 16 * u), style = Stroke(2.2f * u))
        drawRect(p.detalle.copy(alpha = 0.45f), Offset(13 * u, 13 * u), Size(w - 26 * u, h - 26 * u), style = Stroke(1 * u))
        // Los copos de nieve.
        for (k in 0 until 26) {
            val o = Offset(w * azar(83, k), h * azar(83, k + 100))
            val r = (3 + 3 * azar(83, k + 200)) * u
            for (j in 0 until 3) {
                val a = Math.toRadians(30.0 + 60.0 * j)
                val d = Offset(r * cos(a).toFloat(), r * sin(a).toFloat())
                drawLine(p.tenue.copy(alpha = 0.08f), o - d, o + d, 0.7f * u)
            }
        }
        // El cordón de bombillas en festones, bajo el marco de arriba.
        val n = 6
        val paso = (w - 60 * u) / n
        for (k in 0 until n) {
            val a = Offset(30 * u + k * paso, 18 * u)
            drawArc(p.detalle.copy(alpha = 0.3f), 0f, 180f, false, Offset(a.x, a.y - 7 * u), Size(paso, 14 * u), style = Stroke(0.9f * u))
            drawOval(c(BOMBILLAS[k % BOMBILLAS.size]).copy(alpha = 0.55f), Offset(a.x + paso / 2 - 1.8f * u, a.y + 7 * u - 1 * u), Size(3.6f * u, 5 * u))
        }
        // Las ramitas de acebo de las esquinas.
        listOf(Offset(14 * u, 14 * u), Offset(w - 14 * u, 14 * u), Offset(14 * u, h - 14 * u), Offset(w - 14 * u, h - 14 * u)).forEach { o ->
            acebo(o, 24 * u, c(VERDE).copy(alpha = 0.85f), c(ROJO))
        }
        // La hilera de abetitos al pie.
        var x = 34 * u
        while (x < w - 30 * u) {
            val b = h - 20 * u
            for (j in 0 until 3) {
                val s = (7 - j * 1.6f) * u
                drawPath(Path().apply { moveTo(x - s, b - j * 4.5f * u); lineTo(x, b - j * 4.5f * u - s * 1.2f); lineTo(x + s, b - j * 4.5f * u); close() }, p.detalle.copy(alpha = 0.12f))
            }
            x += 24 * u
        }
        if (oscuro && !p.oscura) {
            val fuego = Offset(w * 0.16f, h * 1.02f)
            penumbraCon(fuego, Color(0xFFFF9E57), 0.2f)
            hazCon(fuego, -72f, 34f, h * 0.7f, LLAMA.copy(alpha = 0.035f))
        }
    }

    /** Una guirnalda de abeto al pie de la barra, con su cordón de bombillas (encendidas en modo oscuro y de noche). */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        guirnalda(Offset(-4 * u, h - 6 * u), Offset(w + 4 * u, h - 6 * u), 0f, (w / (36 * u)).toInt().coerceAtLeast(3), oscuro || noche, c(ABETO), c(ABETO_OSC), 11) { c(it) }
    }

    /** La cinta de raso rojo, con su filete de oro y un moño chico en la punta izquierda. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1 * u, 1.8f * u), size, CornerRadius(7 * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(mezcla(ROJO, Color.White, 0.18f)), c(ROJO), c(mezcla(ROJO, Color.Black, 0.25f)))), Offset.Zero, size, CornerRadius(7 * u))
        drawRoundRect(c(ORO), Offset(2.5f * u, 2.5f * u), Size(w - 5 * u, h - 5 * u), CornerRadius(5 * u), style = Stroke(0.8f * u))
        val m = Offset(9 * u, h / 2)
        listOf(-1f, 1f).forEach { d ->
            drawPath(Path().apply { moveTo(m.x, m.y); lineTo(m.x + d * 4.5f * u, m.y - 4 * u); lineTo(m.x + d * 4.5f * u, m.y + 4 * u); close() }, c(ORO))
        }
        drawCircle(c(mezcla(ORO, Color.White, 0.3f)), 1.6f * u, m)
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFFFF4E0)

    // ---------------------------------------------------------------- la libreta de momentos

    /** El cartón rojo de la casita de adviento, el verde de sus puertas, el chocolate de la taza y la galleta de jengibre. */
    private val CASITA = Color(0xFFB3122E)
    private val PUERTA = Color(0xFF1F6B3A)
    private val CHOCOLATE = Color(0xFF5A3420)
    private val JENGIBRE = Color(0xFFB9783E)
    /** El orden revuelto de las 24 casillas, como en los calendarios de verdad. */
    private val ORDEN_CASILLAS = listOf(13, 2, 21, 7, 18, 10, 4, 23, 15, 1, 9, 20, 6, 17, 12, 24, 3, 14, 8, 22, 11, 5, 19, 16)

    override val altoLibreta: Dp = 264.dp

    /** Una ramita de acebo de hojas dentadas (más grande que la del estante): dos hojas y tres bayas en [c]. */
    private fun DrawScope.aceboDentado(c: Offset, s: Float, hoja: Color, baya: Color) {
        listOf(-1f, 1f).forEach { lado ->
            val p = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + lado * s * 0.35f, c.y - s * 0.32f); lineTo(c.x + lado * s * 0.5f, c.y - s * 0.12f); lineTo(c.x + lado * s * 0.75f, c.y - s * 0.3f)
                lineTo(c.x + lado * s * 1.05f, c.y - s * 0.05f); lineTo(c.x + lado * s * 0.75f, c.y + s * 0.08f); lineTo(c.x + lado * s * 0.55f, c.y + s * 0.28f)
                lineTo(c.x + lado * s * 0.35f, c.y + s * 0.12f); close()
            }
            drawPath(p, hoja)
        }
        listOf(Offset(-0.12f, -0.06f), Offset(0.12f, -0.06f), Offset(0f, 0.1f)).forEach { o -> drawCircle(baya, s * 0.13f, Offset(c.x + o.x * s, c.y + o.y * s)) }
    }

    /**
     * El calendario de adviento de pie sobre la mesa de la sala, contra el papel pintado de rayas y la ventana donde nieva:
     * una casita de cartón rojo con su tejado nevado y la chimenea, el banderín crema con el nombre y la cuenta, y las 24
     * casillas en orden revuelto; cada hoja escrita abre una (hasta las 24), con su dulce dentro. Engorda con las hojas: la
     * caja es más honda (se ve su costado) y, pasadas las 24, se le apilan detrás los calendarios de los años anteriores (uno
     * cada 24 hojas, hasta 3). En la mesa, el plato de galletas de jengibre, la taza de chocolate con sus malvaviscos, la vela
     * en su palmatoria y una rama de abeto. Al abrirse se abre la puerta grande de la casita, abajo al centro: sus dos hojas se
     * apartan y dejan ver la estrella dorada con su luz. En modo oscuro, la sala de noche: alumbran la vela (el único
     * degradado), las ventanitas abiertas y la puerta.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0C0908), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C0908), 0.28f) else x
        val mesaY = h * 0.8f
        // El papel pintado de rayas y la ventana con la nieve.
        drawRect(c(PARED), Offset.Zero, Size(w, mesaY))
        var x = 0f; var k = 0
        while (x < w) { drawRect(c(PARED_RAYA).copy(alpha = if (k % 2 == 0) 0.5f else 0.25f), Offset(x, 0f), Size(if (k % 2 == 0) 5 * u else 2 * u, mesaY)); x += 12 * u; k++ }
        val ven = Rect(w * 0.03f, h * 0.06f, w * 0.2f, h * 0.56f)
        drawRect(c(NOGAL), ven.inflate(4 * u).topLeft, ven.inflate(4 * u).size)
        drawRect(c(if (oscuro) CIELO_NOCHE else CIELO_DIA), ven.topLeft, ven.size)
        for (j in 0 until 18) drawCircle(c(NIEVE).copy(alpha = 0.9f), (0.8f + 1.4f * azar(170, j)) * u, Offset(ven.left + ven.width * azar(171, j), ven.top + ven.height * azar(172, j)))
        drawRect(c(NIEVE), Offset(ven.left, ven.bottom - 7 * u), Size(ven.width, 7 * u))
        drawLine(c(NOGAL), Offset(ven.center.x, ven.top), Offset(ven.center.x, ven.bottom), 3 * u)
        drawLine(c(NOGAL), Offset(ven.left, ven.center.y), Offset(ven.right, ven.center.y), 3 * u)
        // La mesa de nogal con su mantel de cuadros en la orilla.
        drawRect(c(NOGAL), Offset(0f, mesaY), Size(w, h - mesaY))
        vetas(Rect(0f, mesaY, w, h), c(NOGAL_VETA).copy(alpha = 0.6f), 173, densidad = 0.8f)
        drawRect(c(NOGAL_LUZ), Offset(0f, mesaY), Size(w, 2.5f * u))
        // La casita: sus medidas y su hondura.
        val W = w * 0.52f
        val casa = Rect(w * 0.26f, h * 0.05f, w * 0.26f + W, mesaY + 4 * u)
        val hondo = (5f + 16f * lleno) * u
        val techo = casa.top + casa.height * 0.2f
        // Los calendarios de años anteriores, apilados detrás.
        val viejos = ((e.hojas - 1) / 24).coerceIn(0, 3)
        for (j in viejos downTo 1) {
            val o = Offset(j * 9 * u, -j * 6 * u)
            drawPath(Path().apply { moveTo(casa.left + o.x, techo + o.y); lineTo(casa.center.x + o.x, casa.top + o.y); lineTo(casa.right + o.x, techo + o.y)
                lineTo(casa.right + o.x, casa.bottom); lineTo(casa.left + o.x, casa.bottom); close() }, c(mezcla(if (j % 2 == 0) CASITA else PUERTA, Color.Black, 0.2f + 0.08f * j)))
            drawPath(Path().apply { moveTo(casa.left + o.x, techo + o.y); lineTo(casa.center.x + o.x, casa.top + o.y); lineTo(casa.right + o.x, techo + o.y) }, c(NIEVE).copy(alpha = 0.7f), style = Stroke(3 * u))
        }
        // La sombra en la mesa y el costado de la caja (más hondo cuantas más hojas).
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(casa.left - 4 * u, casa.bottom - 7 * u), Size(W + hondo + 14 * u, 14 * u))
        drawPath(Path().apply { moveTo(casa.right, techo); lineTo(casa.right + hondo, techo - hondo * 0.5f); lineTo(casa.right + hondo, casa.bottom - hondo * 0.5f); lineTo(casa.right, casa.bottom); close() },
            c(mezcla(CASITA, Color.Black, 0.4f)))
        drawPath(Path().apply { moveTo(casa.center.x, casa.top); lineTo(casa.center.x + hondo, casa.top - hondo * 0.5f); lineTo(casa.right + hondo, techo - hondo * 0.5f); lineTo(casa.right, techo); close() },
            c(NIEVE).copy(alpha = 0.85f))
        // La chimenea con su humo.
        val ch = Rect(casa.left + W * 0.68f, casa.top + casa.height * 0.02f, casa.left + W * 0.78f, casa.top + casa.height * 0.13f)
        drawRect(c(mezcla(CASITA, Color.Black, 0.3f)), ch.topLeft, ch.size)
        drawRect(c(NIEVE), Offset(ch.left - 2 * u, ch.top - 3 * u), Size(ch.width + 4 * u, 4 * u))
        for (j in 0 until 3) drawCircle(c(NIEVE).copy(alpha = 0.35f - 0.08f * j), (4f + 2f * j) * u, Offset(ch.center.x + (3 + j * 5) * u, ch.top - (8 + j * 9) * u))
        // El frente: la fachada roja con su tejado nevado.
        val frente = Path().apply { moveTo(casa.left, techo); lineTo(casa.center.x, casa.top); lineTo(casa.right, techo); lineTo(casa.right, casa.bottom); lineTo(casa.left, casa.bottom); close() }
        drawPath(frente, c(CASITA))
        drawPath(Path().apply { moveTo(casa.left - 6 * u, techo + 4 * u); lineTo(casa.center.x, casa.top - 3 * u); lineTo(casa.right + 6 * u, techo + 4 * u) }, c(NIEVE), style = Stroke(6 * u))
        for (j in 0 until 7) { val f = (j + 0.5f) / 7f; val px = casa.left - 6 * u + (W + 12 * u) * f
            val py = if (f < 0.5f) techo + 4 * u - (techo - casa.top + 7 * u) * f * 2 else casa.top - 3 * u + (techo - casa.top + 7 * u) * (f - 0.5f) * 2
            drawCircle(c(NIEVE), 3 * u, Offset(px, py + 3.5f * u)) }
        drawPath(estrella(Offset(casa.center.x, casa.top + casa.height * 0.1f), 8 * u, 5, 0.42f), c(ORO))
        // El banderín crema con el nombre y la cuenta.
        val ban = Rect(casa.left + W * 0.06f, techo + 6 * u, casa.right - W * 0.06f, techo + 40 * u)
        drawRect(Color.Black.copy(alpha = 0.2f), ban.topLeft + Offset(1 * u, 1.5f * u), ban.size)
        drawPath(Path().apply { moveTo(ban.left - 8 * u, ban.top + 4 * u); lineTo(ban.left, ban.top + 4 * u); lineTo(ban.left, ban.bottom + 4 * u); lineTo(ban.left - 8 * u, ban.bottom + 4 * u); lineTo(ban.left - 4 * u, ban.center.y + 4 * u); close() }, c(mezcla(CREMA, Color.Black, 0.15f)))
        drawPath(Path().apply { moveTo(ban.right + 8 * u, ban.top + 4 * u); lineTo(ban.right, ban.top + 4 * u); lineTo(ban.right, ban.bottom + 4 * u); lineTo(ban.right + 8 * u, ban.bottom + 4 * u); lineTo(ban.right + 4 * u, ban.center.y + 4 * u); close() }, c(mezcla(CREMA, Color.Black, 0.15f)))
        drawRect(cp(CREMA), ban.topLeft, ban.size)
        drawRect(c(ORO), ban.deflate(2.5f * u).topLeft, ban.deflate(2.5f * u).size, style = Stroke(0.8f * u))
        textoEn(medidor, e.nombre, Rect(ban.left + 6 * u, ban.top + 3 * u, ban.right - 6 * u, ban.top + ban.height * 0.62f), letra, c(TINTA), 14f, peso = FontWeight.Bold, minimo = 7.5f)
        textoEn(medidor, e.cuenta, Rect(ban.left + 6 * u, ban.top + ban.height * 0.58f, ban.right - 6 * u, ban.bottom - 3 * u), letra, c(ROJO), 9.5f, minimo = 6f)
        // Las 24 casillas: las abiertas, con su dulce y la puertecita girada; las cerradas, verdes con su número.
        val abiertas = e.hojas.coerceIn(0, 24)
        val cols = 6; val filas = 4
        val rejilla = Rect(casa.left + W * 0.06f, ban.bottom + 6 * u, casa.right - W * 0.06f, casa.bottom - 44 * u)
        val cw = rejilla.width / cols; val chh = rejilla.height / filas
        val dulces = listOf(Color(0xFFFFD27A), Color(0xFF6FB3E0), Color(0xFFE85A6A), Color(0xFF8BD17A))
        for (i in 0 until 24) {
            val col = i % cols; val fila = i / cols
            val o = Offset(rejilla.left + col * cw + cw * 0.1f, rejilla.top + fila * chh + chh * 0.1f); val s = Size(cw * 0.8f, chh * 0.8f)
            val num = ORDEN_CASILLAS[i]
            if (num <= abiertas) {
                drawRect(if (oscuro) mezcla(LLAMA, Color.Black, 0.35f) else Color(0xFF2A1A10), o, s)
                val cc = Offset(o.x + s.width / 2, o.y + s.height / 2)
                if (num % 3 == 0) drawPath(estrella(cc, minOf(s.width, s.height) * 0.32f, 5, 0.45f), c(ORO))
                else drawCircle(c(dulces[num % 4]), minOf(s.width, s.height) * 0.24f, cc)
                drawRect(c(mezcla(PUERTA, Color.White, 0.1f)), Offset(o.x - s.width * 0.22f, o.y), Size(s.width * 0.24f, s.height))
            } else {
                drawRect(c(PUERTA), o, s)
                drawRect(c(ORO).copy(alpha = 0.8f), o, s, style = Stroke(0.8f * u))
                textoEn(medidor, "$num", Rect(o, s), letra, cp(CREMA), 9f, peso = FontWeight.Bold, minimo = 5f)
            }
        }
        // La puerta grande de abajo, con sus dos hojas que se abren.
        val pu = Rect(casa.center.x - 18 * u, casa.bottom - 40 * u, casa.center.x + 18 * u, casa.bottom - 2 * u)
        drawRect(if (oscuro || a > 0f) mezcla(LLAMA, Color(0xFF3A1A08), 0.5f - 0.3f * a) else Color(0xFF2A1A10), pu.topLeft, pu.size)
        if (a > 0.02f) {
            drawCircle(LLAMA.copy(alpha = 0.35f * a), 20 * u, pu.center)
            drawPath(estrella(pu.center, 11 * u * (0.5f + 0.5f * a), 5, 0.42f), cp(ORO))
        }
        val hoja = pu.width / 2 * (1f - a * 0.85f)
        listOf(pu.left to 1f, pu.right to -1f).forEach { (bx, s) ->
            val x0 = if (s > 0) bx else bx - hoja
            drawRect(c(PUERTA), Offset(x0, pu.top), Size(hoja, pu.height))
            drawRect(c(ORO), Offset(x0, pu.top), Size(hoja, pu.height), style = Stroke(1 * u))
            if (hoja > 6 * u) drawCircle(c(ORO), 1.6f * u, Offset(if (s > 0) x0 + hoja - 3.5f * u else x0 + 3.5f * u, pu.center.y))
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.3f * a), Offset(x0, pu.top), Size(hoja, pu.height))
        }
        aceboDentado(Offset(pu.center.x, pu.top - 5 * u), 11 * u, c(VERDE), c(ROJO))
        // La rama de abeto, el plato de galletas a la izquierda; la taza y la vela a la derecha.
        for (j in 0 until 9) drawLine(c(ABETO), Offset(w * 0.02f + j * 6 * u, h * 0.93f), Offset(w * 0.02f + j * 6 * u + 5 * u, h * 0.88f - (j % 2) * 3 * u), 2 * u)
        drawLine(c(NOGAL_VETA), Offset(w * 0.01f, h * 0.94f), Offset(w * 0.2f, h * 0.9f), 1.4f * u)
        val pl = Offset(w * 0.13f, h * 0.84f)
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(pl.x - 26 * u, pl.y - 4 * u), Size(54 * u, 16 * u))
        drawOval(cp(CREMA), Offset(pl.x - 27 * u, pl.y - 8 * u), Size(54 * u, 16 * u))
        drawOval(c(ROJO).copy(alpha = 0.5f), Offset(pl.x - 23 * u, pl.y - 6.5f * u), Size(46 * u, 13 * u), style = Stroke(0.8f * u))
        drawPath(estrella(Offset(pl.x - 9 * u, pl.y - 3 * u), 9 * u, 5, 0.5f), c(JENGIBRE))
        drawPath(estrella(Offset(pl.x - 9 * u, pl.y - 3 * u), 9 * u, 5, 0.5f), c(NIEVE), style = Stroke(0.8f * u))
        val mu = Offset(pl.x + 10 * u, pl.y - 4 * u)
        drawCircle(c(JENGIBRE), 3.2f * u, Offset(mu.x, mu.y - 7 * u))
        drawPath(Path().apply { moveTo(mu.x - 3 * u, mu.y - 4 * u); lineTo(mu.x + 3 * u, mu.y - 4 * u); lineTo(mu.x + 7 * u, mu.y - 1 * u); lineTo(mu.x + 4 * u, mu.y)
            lineTo(mu.x + 5 * u, mu.y + 6 * u); lineTo(mu.x, mu.y + 3 * u); lineTo(mu.x - 5 * u, mu.y + 6 * u); lineTo(mu.x - 4 * u, mu.y); lineTo(mu.x - 7 * u, mu.y - 1 * u); close() }, c(JENGIBRE))
        drawCircle(c(NIEVE), 0.7f * u, Offset(mu.x - 1.2f * u, mu.y - 7.5f * u)); drawCircle(c(NIEVE), 0.7f * u, Offset(mu.x + 1.2f * u, mu.y - 7.5f * u))
        val tz = Offset(w * 0.86f, h * 0.83f)
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(tz.x - 14 * u, tz.y + 10 * u), Size(32 * u, 8 * u))
        drawArc(c(VERDE), -70f, 140f, false, Offset(tz.x + 8 * u, tz.y - 6 * u), Size(14 * u, 14 * u), style = Stroke(3.2f * u))
        drawRect(c(VERDE), Offset(tz.x - 12 * u, tz.y - 8 * u), Size(24 * u, 20 * u))
        drawOval(c(VERDE), Offset(tz.x - 12 * u, tz.y + 8 * u), Size(24 * u, 7 * u))
        drawRect(c(NIEVE).copy(alpha = 0.8f), Offset(tz.x - 12 * u, tz.y - 1 * u), Size(24 * u, 3 * u))
        drawOval(cp(CREMA), Offset(tz.x - 12 * u, tz.y - 11 * u), Size(24 * u, 7 * u))
        drawOval(c(CHOCOLATE), Offset(tz.x - 10 * u, tz.y - 10 * u), Size(20 * u, 5 * u))
        listOf(-4f, 2f, 5f).forEach { dx -> drawRect(cp(NIEVE), Offset(tz.x + dx * u - 1.8f * u, tz.y - 9.6f * u), Size(3.6f * u, 3 * u)) }
        // La vela en su palmatoria de latón.
        val ve = Offset(w * 0.9f, h * 0.48f)
        drawOval(c(ORO), Offset(ve.x - 13 * u, mesaY - 6 * u), Size(26 * u, 8 * u))
        drawLine(c(ORO), Offset(ve.x, mesaY - 4 * u), Offset(ve.x, ve.y + 46 * u), 3 * u)
        drawOval(c(ORO), Offset(ve.x - 8 * u, ve.y + 44 * u), Size(16 * u, 5 * u))
        drawRect(cp(CERA), Offset(ve.x - 5 * u, ve.y + 8 * u), Size(10 * u, 38 * u))
        drawRect(Color.Black.copy(alpha = 0.1f), Offset(ve.x + 2 * u, ve.y + 8 * u), Size(3 * u, 38 * u))
        drawLine(c(TINTA), Offset(ve.x, ve.y + 8 * u), Offset(ve.x, ve.y + 4 * u), 0.8f * u)
        drawPath(Path().apply { moveTo(ve.x, ve.y + 5 * u); quadraticTo(ve.x - 4.5f * u, ve.y - 1 * u, ve.x, ve.y - 9 * u); quadraticTo(ve.x + 4.5f * u, ve.y - 1 * u, ve.x, ve.y + 5 * u) }, LLAMA)
        drawCircle(Color.White.copy(alpha = 0.8f), 1.4f * u, Offset(ve.x, ve.y + 1 * u))
        if (oscuro) drawRect(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.32f), LLAMA.copy(alpha = 0.1f), Color.Transparent), Offset(ve.x, ve.y), w * 0.8f))
    }

    /**
     * La pestaña del día es una casilla de adviento: la puertecita roja o verde (según la semilla) con su marco dorado, la
     * letra de la semana arriba, el número grande y, colgando abajo, la bola de árbol en el color del ánimo con su casquillo
     * dorado (sin ánimo, un copo de nieve).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1 * u, 1.5f * u), r.size)
        drawRect(c(if ((e.semilla and 1) == 0) PUERTA else CASITA), r.topLeft, r.size)
        drawRect(c(ORO), r.deflate(2 * u).topLeft, r.deflate(2 * u).size, style = Stroke(1 * u))
        drawCircle(c(ORO), 1.4f * u, Offset(r.right - 5.5f * u, r.top + 38 * u))
        textoEn(medidor, e.semana, Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.top + 12 * u), letra, c(CREMA).copy(alpha = 0.85f), 7.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 3 * u, r.top + 12 * u, r.right - 3 * u, r.top + 33 * u), letra, c(CREMA), 19f, peso = FontWeight.Bold, minimo = 9f)
        val b = Offset(r.center.x, r.bottom - 9 * u)
        if (e.tono != null) {
            drawLine(c(ORO), Offset(b.x, r.top + 34 * u), Offset(b.x, b.y - 6 * u), 0.7f * u)
            drawRect(c(ORO), Offset(b.x - 2.2f * u, b.y - 7.5f * u), Size(4.4f * u, 2.6f * u))
            drawCircle(e.tono, 5.4f * u, b)
            drawCircle(Color.White.copy(alpha = 0.45f), 1.5f * u, b + Offset(-2 * u, -1.8f * u))
        } else for (k in 0 until 3) rotate(k * 60f, b) { drawLine(c(NIEVE).copy(alpha = 0.8f), Offset(b.x - 5 * u, b.y), Offset(b.x + 5 * u, b.y), 0.9f * u) }
    }

    /** El mes en una cinta de raso roja que cruza la franja, con su ribete dorado y una ramita de acebo en cada punta. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(0f, 6 * u, w, h - 6 * u)
        drawRect(Color.Black.copy(alpha = 0.18f), b.topLeft + Offset(0f, 1.6f * u), b.size)
        drawRect(c(ROJO), b.topLeft, b.size)
        drawRect(Color.White.copy(alpha = 0.1f), Offset(b.left, b.top + 2 * u), Size(b.width, 4 * u))
        drawLine(c(ORO), Offset(b.left, b.top + 1.2f * u), Offset(b.right, b.top + 1.2f * u), 1 * u)
        drawLine(c(ORO), Offset(b.left, b.bottom - 1.2f * u), Offset(b.right, b.bottom - 1.2f * u), 1 * u)
        aceboDentado(Offset(18 * u, h / 2), 12 * u, c(VERDE), c(ORO))
        aceboDentado(Offset(w - 18 * u, h / 2), 12 * u, c(VERDE), c(ORO))
        textoEn(medidor, texto, Rect(40 * u, b.top + 2 * u, w - 40 * u, b.bottom - 2 * u), letra, c(CREMA), 13f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El rojo del papel de regalo, la cinta de raso verde, el papel de seda y la madera roja del trineo. */
    private val REGALO = Color(0xFFB71C1C)
    private val REGALO_OSC = Color(0xFF7A1010)
    private val RASO = Color(0xFF1B5E20)
    private val RASO_LUZ = Color(0xFF3F8A44)
    private val SEDA = Color(0xFFF6F1E6)
    private val ARPILLERA = Color(0xFFB08A5A)

    /** El papel de regalo: rojo con estrellitas de oro y puntos de nieve. */
    private fun DrawScope.papelRegalo(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = density
        drawRect(t(REGALO, oscuro, false), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val paso = 13 * u
            var y = r.top + paso * 0.4f; var fila = 0
            while (y < r.bottom + paso) {
                var x = r.left + (if (fila % 2 == 0) 0f else paso / 2) + paso * 0.3f
                while (x < r.right + paso) {
                    drawPath(estrella(Offset(x, y), 2.4f * u, 5, 0.45f), t(ORO, oscuro, false).copy(alpha = 0.85f))
                    drawCircle(t(NIEVE, oscuro, false).copy(alpha = 0.55f), 0.8f * u, Offset(x + paso * 0.5f, y + paso * 0.25f))
                    x += paso
                }
                y += paso * 0.75f; fila++
            }
        }
    }

    /** El sello de cera dorada, con la estrella de ocho puntas y su aro. */
    private fun DrawScope.selloEstrella(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val gota = Path().apply {
            for (i in 0 until 16) {
                val a = (2 * PI * i / 16).toFloat()
                val rr = r * (0.92f + 0.14f * azar(i, 81))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        translate(r * 0.06f, r * 0.1f) { drawPath(gota, Color.Black.copy(alpha = 0.3f * alfa)) }
        drawPath(gota, k(ORO))
        drawCircle(k(mezcla(ORO, Color.Black, 0.3f)), r * 0.74f, c, style = Stroke(r * 0.07f))
        drawPath(estrella(c, r * 0.58f, 8, 0.45f), k(mezcla(ORO, Color.Black, 0.25f)))
        drawPath(estrella(c, r * 0.4f, 8, 0.45f), k(mezcla(ORO, Color.White, 0.35f)))
        drawArc(k(Color.White.copy(alpha = 0.4f)), 200f, 60f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    /** El moño de raso: dos lazadas, el nudo y las dos colas en pico. */
    private fun DrawScope.mono(c: Offset, s: Float, oscuro: Boolean) {
        val u = density
        fun k(x: Color) = t(x, oscuro, false)
        listOf(-1f, 1f).forEach { l0 ->
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + l0 * s * 0.5f, c.y + s * 0.95f); lineTo(c.x + l0 * s * 0.3f, c.y + s * 0.8f); lineTo(c.x + l0 * s * 0.18f, c.y + s * 0.98f); close() }, k(RASO))
            val lazo = Path().apply { moveTo(c.x, c.y); cubicTo(c.x + l0 * s * 0.4f, c.y - s * 0.75f, c.x + l0 * s * 1.05f, c.y - s * 0.4f, c.x + l0 * s * 0.85f, c.y + s * 0.05f); close() }
            drawPath(lazo, k(RASO))
            drawPath(lazo, k(RASO_LUZ), style = Stroke(0.8f * u))
        }
        drawOval(k(RASO_LUZ), Offset(c.x - s * 0.17f, c.y - s * 0.14f), Size(s * 0.34f, s * 0.28f))
    }

    /**
     * La caja de regalo: el papel rojo con estrellitas de oro, la tapa más ancha con su canto y, abajo, la etiqueta de regalo
     * de cartulina crema atada con su hilo, donde van a quién va y las fechas. La cierra el sello de cera dorada con la
     * estrella, montado entre la tapa y la caja; las de al futuro, además, la cinta de raso verde en cruz con su moño. Al
     * abrirla, el sello se parte (media mitad sube con la tapa), la tapa se levanta y se recuesta detrás, y entre el papel de
     * seda sube la carta con su ramita de acebo. En modo oscuro, la caja se hunde en la penumbra de la sala; la etiqueta,
     * clara, se sigue leyendo.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 3 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 3 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val borde = top + H * 0.2f
        val tapaH = H * 0.3f
        val a = e.abrir.coerceIn(0f, 1f)
        val xr = l + W * 0.11f
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(l + 2 * u, borde + 3 * u), Size(W, bot - borde))
        // La tapa, recostada detrás de la caja abierta.
        fun DrawScope.tapaCaja(conSello: Boolean) {
            val tr = Rect(l - 3 * u, top, r + 3 * u, top + tapaH)
            drawRect(Color.Black.copy(alpha = 0.25f), tr.topLeft + Offset(0f, 2 * u), tr.size)
            papelRegalo(tr, oscuro, e.semilla + 1)
            drawRect(c(REGALO_OSC), Offset(tr.left, tr.bottom - 2.5f * u), Size(tr.width, 2.5f * u))
            drawRect(c(Color.White).copy(alpha = 0.15f), tr.topLeft, Size(tr.width, 1.5f * u))
            if (e.cordon > 0f) {
                val f = (e.cordon / 0.4f).coerceIn(0f, 1f)
                drawRect(c(RASO), Offset(xr - 4 * u, tr.top), Size(8 * u, tr.height * f))
                drawRect(c(RASO_LUZ), Offset(xr - 2.5f * u, tr.top), Size(1.2f * u, tr.height * f))
                val f2 = ((e.cordon - 0.4f) / 0.35f).coerceIn(0f, 1f)
                drawRect(c(RASO), Offset(tr.left, tr.center.y - 4 * u), Size(tr.width * f2, 8 * u))
                drawRect(c(RASO_LUZ), Offset(tr.left, tr.center.y - 2.5f * u), Size(tr.width * f2, 1.2f * u))
                val m = ((e.cordon - 0.8f) / 0.2f).coerceIn(0f, 1f)
                if (m > 0f) scale(m, m, Offset(xr, tr.center.y)) { mono(Offset(xr, tr.center.y), tapaH * 0.6f, oscuro) }
            }
            if (conSello) acebo(Offset(r - W * 0.12f, tr.center.y), tapaH * 0.5f, c(ABETO), c(ROJO))
        }
        if (a > 0.5f) {
            val f = (a - 0.5f) / 0.5f
            translate(W * 0.12f * f, -H * 0.12f - H * 0.08f * f) { rotate(8f * f, Offset(cx, top + tapaH / 2)) { tapaCaja(false) } }
        }
        // El interior de la caja: verde, con el papel de seda arrugado.
        drawRect(c(RASO), Offset(l, borde - 2 * u), Size(W, H * 0.3f))
        for (j in 0 until 7) {
            val p = Offset(l + W * (0.05f + 0.9f * j / 6f), borde + 2 * u)
            drawPath(Path().apply { moveTo(p.x - 10 * u, p.y + 4 * u); lineTo(p.x - 3 * u, p.y - 4 * u - 3 * u * azar(e.semilla, j)); lineTo(p.x + 2 * u, p.y + 1 * u); lineTo(p.x + 8 * u, p.y - 5 * u); lineTo(p.x + 12 * u, p.y + 4 * u); close() }, c(SEDA))
        }
        // La carta, con su ramita de acebo.
        if (e.subir > 0f || a > 0.3f) {
            val h = hojaCo(e, 0.8f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 9 * u, 5.5f * u, 7 * u, oscuro)
            acebo(Offset(h.right - 7 * u, h.top + 6 * u), 9 * u, c(ABETO), c(ROJO))
            drawLine(c(ORO), Offset(h.left + 4 * u, h.top + 3 * u), Offset(h.left + h.width * 0.4f, h.top + 3 * u), 0.8f * u)
        }
        // El cuerpo de la caja.
        val caja = Rect(l, borde, r, bot)
        papelRegalo(caja, oscuro, e.semilla)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(r - 5 * u, borde), Size(5 * u, bot - borde))
        drawRect(c(Color.White).copy(alpha = 0.12f), Offset(l, borde), Size(W, 1.5f * u))
        // La cinta vertical del cuerpo.
        if (e.cordon > 0f) {
            val f = (e.cordon / 0.4f).coerceIn(0f, 1f)
            drawRect(c(RASO), Offset(xr - 4 * u, borde), Size(8 * u, (bot - borde) * f))
            drawRect(c(RASO_LUZ), Offset(xr - 2.5f * u, borde), Size(1.2f * u, (bot - borde) * f))
        }
        // La etiqueta de regalo, con su hilo rojo.
        val et = Rect(l + W * 0.2f, top + H * 0.56f, r - W * 0.04f, bot - 5 * u)
        drawLine(c(ROJO), Offset(et.left + 3 * u, et.center.y), Offset(xr + 2 * u, top + tapaH + 3 * u), 1 * u)
        val pe = etiquetaRegalo(et, et.height * 0.3f)
        translate(1 * u, 1.5f * u) { drawPath(pe, Color.Black.copy(alpha = 0.25f)) }
        drawPath(pe, cremaTapa(oscuro, false))
        drawPath(etiquetaRegalo(et.deflate(2 * u), et.height * 0.24f), c(ORO), style = Stroke(0.7f * u))
        drawCircle(c(ROJO), 1.6f * u, Offset(et.left + 4 * u, et.center.y), style = Stroke(0.8f * u))
        textoEn(medidor, e.linea1, Rect(et.left + 8 * u, et.top + 2 * u, et.right - 4 * u, et.top + et.height * 0.58f), letra, TINTA, 11.5f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(et.left + 8 * u, et.top + et.height * 0.55f, et.right - 4 * u, et.bottom - 2 * u), letra, mezcla(TINTA, ROJO, 0.4f), 9f, minimo = 6f)
        // La tapa, cerrada o levantándose.
        val sube = if (a <= 0.5f) H * 0.3f * (a / 0.5f) else 0f
        if (a <= 0.5f) translate(0f, -sube) { tapaCaja(true) }
        // El sello de cera dorada, entre la tapa y la caja.
        val selloR = H * 0.13f
        val y0 = top + tapaH
        if (e.alfaSello > 0f) {
            if (a <= 0.5f) brilloSelloCo(Offset(cx, y0 - sube), selloR, c(LLAMA), e.brillo)
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.2f * e.romper, -sube - selloR * 0.3f * e.romper),
                Offset(selloR * 0.2f * e.romper, selloR * 0.3f * e.romper), e.semilla, conArriba = a <= 0.5f) { selloEstrella(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.235f, 0.83f, 0.39f)

    override val altoBuzon: Dp = 212.dp

    /** Una carta chica asomando del saco: lista (su sello de oro brilla), atada con cinta y moño, o abierta. */
    private fun DrawScope.cartaSaco(r: Rect, tipo: Int, oscuro: Boolean, giro: Float, semilla: Int) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        rotate(giro, Offset(r.center.x, r.bottom)) {
            if (tipo == 2) {
                drawRect(c(CREMA), Offset(r.left + r.width * 0.12f, r.top - r.height * 0.45f), Size(r.width * 0.76f, r.height * 0.7f))
                for (j in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, r.top - r.height * (0.32f - j * 0.12f)), Offset(r.right - r.width * 0.2f, r.top - r.height * (0.32f - j * 0.12f)), 0.6f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1 * u, 1 * u), r.size)
            drawRect(c(if (semilla % 2 == 0) REGALO else VERDE), r.topLeft, r.size)
            val punta = if (tipo == 2) r.top - r.height * 0.35f else r.top + r.height * 0.5f
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, punta); lineTo(r.right, r.top) }, c(ORO), style = Stroke(0.8f * u))
            if (tipo == 1) {
                drawRect(c(ORO), Offset(r.left, r.top + r.height * 0.55f), Size(r.width, 2 * u))
                drawRect(c(ORO), Offset(r.center.x - 1 * u, r.top), Size(2 * u, r.height))
                drawCircle(c(ORO), 2.4f * u, Offset(r.center.x, r.top + r.height * 0.56f))
            }
            if (tipo != 2) drawCircle(c(if (tipo == 0) mezcla(ORO, Color.White, 0.3f) else ORO), r.width * 0.12f, Offset(r.center.x, r.top + r.height * 0.5f))
        }
    }

    /**
     * El trineo del correo de Navidad, sobre la nieve: la caja de madera roja con sus volutas doradas y los patines curvos
     * de hierro que se enroscan delante; atrás, el saco de arpillera atado, del que asoman las cartas (hasta diez): las que
     * ya se pueden abrir, con su sello de oro que brilla (el primer degradado); las selladas, con cinta y moño; las
     * abiertas, con la carta afuera. En el costado del trineo cuelgan tres etiquetas de regalo con la cuenta de cada clase.
     * Cae la nieve; un abeto nevado detrás. En modo oscuro, la noche de Navidad: el cielo con estrellas y el farolillo del
     * trineo encendido (el segundo degradado).
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // El cielo y la nieve del suelo.
        drawRect(if (oscuro) mezcla(CIELO_NOCHE, Color.Black, 0.25f) else CIELO_DIA)
        if (oscuro) for (j in 0 until 24) drawCircle(NIEVE.copy(alpha = 0.4f + 0.5f * azar(131, j)), (0.5f + 0.7f * azar(132, j)) * u, Offset(w * azar(133, j), h * 0.5f * azar(134, j)))
        val suelo = h * 0.8f
        // El abeto nevado de atrás.
        val ab = Offset(w * 0.88f, suelo)
        for (k in 0 until 4) {
            val y = ab.y - 18 * u - k * 22 * u; val an = (34 - k * 7) * u
            drawPath(Path().apply { moveTo(ab.x, y - 30 * u); lineTo(ab.x + an, y + 4 * u); lineTo(ab.x - an, y + 4 * u); close() }, c(ABETO_OSC))
            drawPath(Path().apply { moveTo(ab.x, y - 30 * u); lineTo(ab.x + an * 0.5f, y - 14 * u); lineTo(ab.x - an * 0.4f, y - 12 * u); close() }, c(NIEVE).copy(alpha = 0.8f))
        }
        drawPath(Path().apply { moveTo(0f, suelo); quadraticTo(w * 0.3f, suelo - 8 * u, w * 0.6f, suelo - 2 * u); quadraticTo(w * 0.85f, suelo + 4 * u, w, suelo - 4 * u); lineTo(w, h); lineTo(0f, h); close() }, c(NIEVE))
        // El trineo.
        val x0 = w * 0.08f; val x1 = w * 0.8f
        val yt = h * 0.42f; val yb = h * 0.76f
        // Los patines.
        val patin = Path().apply {
            moveTo(x0 - 4 * u, yb + 10 * u); lineTo(x1, yb + 10 * u)
            cubicTo(x1 + 30 * u, yb + 10 * u, x1 + 34 * u, yt + 10 * u, x1 + 14 * u, yt + 6 * u)
            cubicTo(x1 + 4 * u, yt + 4 * u, x1 + 4 * u, yt + 16 * u, x1 + 13 * u, yt + 16 * u)
        }
        drawPath(patin, c(HIERRO_TRINEO), style = Stroke(3 * u, cap = StrokeCap.Round))
        listOf(0.15f, 0.5f, 0.85f).forEach { f -> val px = x0 + (x1 - x0) * f; drawLine(c(HIERRO_TRINEO), Offset(px, yb), Offset(px, yb + 10 * u), 2.2f * u) }
        // El saco, atrás.
        val saco = Rect(x0 + 6 * u, yt - 50 * u, x0 + (x1 - x0) * 0.62f, yt + 14 * u)
        // Las cartas, que asoman del saco.
        val cartas = cartasBuzonCo(e, 10)
        val cw = (saco.width - 10 * u) / 10f
        cartas.forEachIndexed { i, tipo ->
            val bw = cw * 1.5f; val bh = bw * 1.05f
            val bx = saco.left + 5 * u + cw * (i + 0.5f)
            val by = saco.top + 12 * u + (kotlin.math.abs(i - 4.5f) / 4.5f) * 8 * u
            val rr = Rect(bx - bw / 2, by - bh, bx + bw / 2, by + 6 * u)
            if (tipo == 0) mancha(Offset(bx, by - bh * 0.4f), bw * 1.1f, c(LLAMA).copy(alpha = 0.55f))
            cartaSaco(rr, tipo, oscuro, (i - 4.5f) * 4f + (azar(135, i) - 0.5f) * 8f, i)
        }
        val sacoP = Path().apply {
            moveTo(saco.left, saco.top + 10 * u); quadraticTo(saco.center.x, saco.top + 2 * u, saco.right, saco.top + 10 * u)
            quadraticTo(saco.right + 12 * u, saco.center.y, saco.right - 4 * u, saco.bottom); lineTo(saco.left + 4 * u, saco.bottom)
            quadraticTo(saco.left - 12 * u, saco.center.y, saco.left, saco.top + 10 * u); close()
        }
        drawPath(sacoP, c(ARPILLERA))
        clipPath(sacoP) {
            var yy = saco.top; while (yy < saco.bottom) { drawLine(c(mezcla(ARPILLERA, Color.Black, 0.2f)).copy(alpha = 0.4f), Offset(saco.left - 10 * u, yy), Offset(saco.right + 10 * u, yy), 0.6f * u); yy += 2.5f * u }
            var xx = saco.left - 10 * u; while (xx < saco.right + 10 * u) { drawLine(c(mezcla(ARPILLERA, Color.Black, 0.2f)).copy(alpha = 0.3f), Offset(xx, saco.top), Offset(xx, saco.bottom), 0.6f * u); xx += 2.5f * u }
        }
        drawPath(Path().apply { moveTo(saco.left + 2 * u, saco.top + 14 * u); quadraticTo(saco.center.x, saco.top + 20 * u, saco.right - 2 * u, saco.top + 14 * u) }, c(ROJO), style = Stroke(2 * u))
        // La caja del trineo, con su respaldo curvo y las volutas.
        val cuerpo = Path().apply {
            moveTo(x0, yt - 14 * u); quadraticTo(x0 - 8 * u, yt - 26 * u, x0 + 4 * u, yt - 30 * u); quadraticTo(x0 + 12 * u, yt - 26 * u, x0 + 8 * u, yt - 4 * u)
            lineTo(x1 - 10 * u, yt + 4 * u); quadraticTo(x1 + 10 * u, yt - 10 * u, x1 + 8 * u, yt + 8 * u)
            lineTo(x1, yb); lineTo(x0 + 6 * u, yb); quadraticTo(x0, yb, x0, yb - 8 * u); close()
        }
        translate(2 * u, 3 * u) { drawPath(cuerpo, Color.Black.copy(alpha = 0.25f)) }
        drawPath(cuerpo, c(ROJO))
        drawPath(cuerpo, c(ORO), style = Stroke(1.6f * u))
        drawLine(c(mezcla(ROJO, Color.Black, 0.3f)), Offset(x0 + 8 * u, yt + 6 * u), Offset(x1 - 4 * u, yt + 12 * u), 1 * u)
        listOf(Offset(x0 + 14 * u, yb - 12 * u), Offset(x1 - 12 * u, yb - 12 * u)).forEachIndexed { i, p ->
            val s = if (i == 0) 1f else -1f
            drawPath(Path().apply { moveTo(p.x, p.y); cubicTo(p.x + s * 10 * u, p.y - 10 * u, p.x + s * 18 * u, p.y + 2 * u, p.x + s * 10 * u, p.y + 4 * u); quadraticTo(p.x + s * 6 * u, p.y + 2 * u, p.x + s * 9 * u, p.y - 1 * u) },
                c(ORO), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        // Las tres etiquetas de regalo con la cuenta.
        val cuentas = listOf("POR ABRIR" to e.listas, "SELLADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val zona = Rect(x0 + 34 * u, yt + 12 * u, x1 - 30 * u, yb - 4 * u)
        val ew = zona.width / 3
        cuentas.forEachIndexed { i, (txt, n) ->
            val et = Rect(zona.left + i * ew + 3 * u, zona.top + 4 * u, zona.left + (i + 1) * ew - 3 * u, zona.bottom)
            drawLine(c(ORO), Offset(et.center.x, zona.top - 4 * u), Offset(et.left + 5 * u, et.center.y), 0.8f * u)
            val pe = etiquetaRegalo(et, et.height * 0.28f)
            translate(0.8f * u, 1.2f * u) { drawPath(pe, Color.Black.copy(alpha = 0.25f)) }
            drawPath(pe, cremaTapa(oscuro, false))
            drawCircle(c(ROJO), 1.5f * u, Offset(et.left + 5 * u, et.center.y), style = Stroke(0.8f * u))
            textoEn(medidor, txt, Rect(et.left + 9 * u, et.top + 2 * u, et.right - 2 * u, et.top + et.height * 0.45f), letra, TINTA, 8f, peso = FontWeight.SemiBold, minimo = 4.5f)
            textoEn(medidor, "$n", Rect(et.left + 9 * u, et.top + et.height * 0.4f, et.right - 2 * u, et.bottom - 1 * u), LetraExtra.cinzelDeco, if (i == 0 && n > 0) ROJO else TINTA, 14f, peso = FontWeight.Bold, minimo = 6f)
        }
        // El farolillo del trineo.
        val fa = Offset(x1 + 2 * u, yt - 18 * u)
        if (oscuro) mancha(fa, w * 0.3f, LLAMA.copy(alpha = 0.3f))
        drawLine(c(HIERRO_TRINEO), Offset(fa.x, fa.y + 8 * u), Offset(fa.x, yt + 4 * u), 1.4f * u)
        drawRect(c(ORO), Offset(fa.x - 5 * u, fa.y - 9 * u), Size(10 * u, 2.5f * u))
        drawRect(if (oscuro) LLAMA else c(CERA), Offset(fa.x - 4 * u, fa.y - 6.5f * u), Size(8 * u, 12 * u))
        drawRect(c(ORO), Offset(fa.x - 4 * u, fa.y - 6.5f * u), Size(8 * u, 12 * u), style = Stroke(1 * u))
        // La nieve que cae.
        for (j in 0 until 30) drawCircle(NIEVE.copy(alpha = if (oscuro) 0.75f else 0.9f), (0.8f + 1.2f * azar(136, j)) * u, Offset(w * azar(137, j), h * azar(138, j)))
    }

    private val HIERRO_TRINEO = Color(0xFF2E2A2A)
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
 * La tapa es esa bitácora de goma de frente, con su pinza, la placa «BITÁCORA DE INMERSIÓN», la pizarra blanca, el ojo
 * de buey con su medusa y la regla de profundidad; al abrirla, la tapa se levanta desde la pinza.
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

    // ---------------------------------------------------------------- la tapa

    private val PIZARRA = Color(0xFFF1F0E8)

    override val altoTapa: Dp = 190.dp
    override val cajaTapa: Rect = Rect(0.1f, 0.365f, 0.75f, 0.715f)
    override val letraTapa: FontFamily = LetraExtra.atkinson
    override val tamTapa: Float = 13f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra bajo el agua" else "$n palabras bajo el agua"

    /** La pizarra blanca de la tapa, donde va el extracto: hundida en oscuro y azulada de noche, pero clara. */
    private fun pizarraTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PIZARRA
        if (oscuro) r = mezcla(r, Color(0xFF07090B), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.14f)
        return r
    }

    /** Una medusa: la campana, su halo si brilla y tres tentáculos ondulados. */
    private fun DrawScope.medusa(c0: Offset, s: Float, color: Color, brilla: Boolean) {
        val u = density
        if (brilla) destello(Offset(c0.x, c0.y - s * 0.15f), s * 0.16f, color)
        drawArc(color, 180f, 180f, true, Offset(c0.x - s * 0.4f, c0.y - s * 0.4f), Size(s * 0.8f, s * 0.6f))
        drawArc(Color.Black.copy(alpha = 0.2f), 0f, 180f, false, Offset(c0.x - s * 0.4f, c0.y - s * 0.2f), Size(s * 0.8f, s * 0.2f), style = Stroke(0.6f * u))
        for (j in 0 until 3) {
            val x = c0.x - s * 0.22f + s * 0.22f * j
            drawPath(Path().apply {
                moveTo(x, c0.y - s * 0.1f); quadraticTo(x + s * 0.1f, c0.y + s * 0.15f, x, c0.y + s * 0.3f); quadraticTo(x - s * 0.1f, c0.y + s * 0.45f, x, c0.y + s * 0.6f)
            }, color.copy(alpha = 0.75f), style = Stroke(0.6f * u))
        }
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val goma = c(GOMAS[(e.semilla and 0x7fffffff) % GOMAS.size])
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val cu = Rect(w * 0.02f, h * 0.085f, w * 0.955f, h * 0.965f)
        // De noche, con la cabina apagada, la bioluminiscencia sube desde abajo (el único degradado).
        if (e.noche) mancha(Offset(w * 0.04f, h), w * 0.6f, BIOLUZ.copy(alpha = 0.14f))
        drawRoundRect(Color.Black.copy(alpha = 0.25f), cu.topLeft + Offset(3 * u, 5 * u), cu.size, CornerRadius(10 * u))
        // La tapa de atrás y las hojas de la libreta que asoman por el canto: más cuanto más escribiste.
        drawRoundRect(mezcla(goma, Color.Black, 0.25f), cu.topLeft + Offset(1.5f * u, 2.5f * u), cu.size, CornerRadius(10 * u))
        val hojas = 1 + (lleno * 4).toInt()
        for (j in 0 until hojas) {
            val x = cu.right + 1.2f * u + j * 1.3f * u
            drawLine(c(ALUMINIO).copy(alpha = 0.9f), Offset(x, cu.top + 12 * u), Offset(x, cu.bottom - 8 * u), 0.8f * u)
        }
        // Por dentro (se ve cuando se levanta la tapa): la hoja de plástico blanco con la tabla de inmersiones y el lápiz atado.
        if (a > 0.01f) {
            val hoja = Rect(cu.left + 8 * u, cu.top + 10 * u, cu.right - 8 * u, cu.bottom - 8 * u)
            drawRoundRect(c(PIZARRA), hoja.topLeft, hoja.size, CornerRadius(4 * u))
            drawRect(c(ALUMINIO).copy(alpha = 0.6f), hoja.topLeft, Size(hoja.width, 10 * u))
            var ly = hoja.top + 19 * u
            while (ly < hoja.bottom - 2 * u) { drawLine(c(TINTA).copy(alpha = 0.2f), Offset(hoja.left, ly), Offset(hoja.right, ly), 0.5f * u); ly += 9 * u }
            listOf(0.22f, 0.44f, 0.66f).forEach { f -> drawLine(c(TINTA).copy(alpha = 0.2f), Offset(hoja.left + hoja.width * f, hoja.top), Offset(hoja.left + hoja.width * f, hoja.bottom), 0.5f * u) }
            drawLine(c(TINTA), Offset(hoja.right - hoja.width * 0.22f, hoja.bottom - 10 * u), Offset(hoja.right - hoja.width * 0.06f, hoja.top + 24 * u), 2.4f * u)
            drawLine(c(ROJO).copy(alpha = 0.7f), Offset(hoja.right - hoja.width * 0.06f, hoja.top + 24 * u), Offset(hoja.right - 4 * u, hoja.top + 4 * u), 0.7f * u)
        }
        // La tapa de goma, que se levanta desde la pinza al abrirse.
        val sy = 1f - a
        if (sy > 0.02f) scale(1f, sy, Offset(cu.center.x, cu.top)) {
            drawRoundRect(goma, cu.topLeft, cu.size, CornerRadius(10 * u))
            var py = cu.top + 4 * u
            while (py < cu.bottom - 2 * u) {
                var px = cu.left + 4 * u
                while (px < cu.right - 2 * u) { drawCircle(Color.Black.copy(alpha = 0.08f), 0.6f * u, Offset(px, py)); px += 6 * u }
                py += 6 * u
            }
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(cu.left, cu.top + 8 * u), Size(5 * u, cu.height - 16 * u))
            drawRect(Color.White.copy(alpha = 0.1f), Offset(cu.left + 10 * u, cu.top + 1 * u), Size(cu.width - 20 * u, 2 * u))
            // La placa de aluminio remachada con el rótulo de la bitácora.
            val pl = Rect(w * 0.07f, h * 0.18f, w * 0.78f, h * 0.3f)
            drawRect(Color.Black.copy(alpha = 0.25f), pl.topLeft + Offset(1 * u, 1.5f * u), pl.size)
            drawRect(c(ALUMINIO), pl.topLeft, pl.size)
            drawRect(Color.White.copy(alpha = 0.35f), pl.topLeft, Size(pl.width, 1.2f * u))
            drawRect(c(TINTA).copy(alpha = 0.4f), pl.topLeft, pl.size, style = Stroke(0.6f * u))
            listOf(pl.left + 6 * u, pl.right - 6 * u).forEach { x ->
                drawCircle(c(TINTA).copy(alpha = 0.5f), 1.7f * u, Offset(x, pl.center.y))
                drawCircle(Color.White.copy(alpha = 0.5f), 0.6f * u, Offset(x - 0.5f * u, pl.center.y - 0.5f * u))
            }
            textoEn(medidor, "BITÁCORA DE INMERSIÓN", Rect(pl.left + 12 * u, pl.top, pl.right - 12 * u, pl.bottom), letra, c(TINTA), 11f, peso = FontWeight.Bold, espaciado = 1.4f, minimo = 6.5f)
            // La pizarra blanca atornillada: el extracto, la fecha de la inmersión y las palabras al pie.
            val sl = Rect(w * 0.07f, h * 0.33f, w * 0.78f, h * 0.915f)
            drawRoundRect(Color.Black.copy(alpha = 0.25f), sl.topLeft + Offset(1 * u, 1.5f * u), sl.size, CornerRadius(4 * u))
            drawRoundRect(pizarraTapa(oscuro, e.noche), sl.topLeft, sl.size, CornerRadius(4 * u))
            drawRoundRect(c(TINTA).copy(alpha = 0.3f), sl.topLeft, sl.size, CornerRadius(4 * u), style = Stroke(0.7f * u))
            listOf(Offset(sl.left + 5 * u, sl.top + 5 * u), Offset(sl.right - 5 * u, sl.top + 5 * u), Offset(sl.left + 5 * u, sl.bottom - 5 * u), Offset(sl.right - 5 * u, sl.bottom - 5 * u)).forEach { o ->
                drawCircle(c(ALUMINIO), 2 * u, o)
                drawCircle(c(TINTA).copy(alpha = 0.4f), 2 * u, o, style = Stroke(0.5f * u))
                drawLine(c(TINTA).copy(alpha = 0.55f), Offset(o.x - 1.3f * u, o.y + 0.6f * u), Offset(o.x + 1.3f * u, o.y - 0.6f * u), 0.6f * u)
            }
            drawLine(c(TINTA).copy(alpha = 0.25f), Offset(sl.left + 8 * u, h * 0.732f), Offset(sl.right - 8 * u, h * 0.732f), 0.6f * u)
            textoEn(medidor, e.fecha, Rect(sl.left + 8 * u, h * 0.742f, sl.right - 8 * u, h * 0.815f), letra, c(TINTA), 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(sl.left + 8 * u, h * 0.82f, sl.right - 8 * u, h * 0.888f), letra, c(TINTA).copy(alpha = 0.7f), 9f, minimo = 6f)
            }
            // El ojo de buey de latón con sus pernos, y detrás del cristal la medusa y la nieve marina.
            val pc = Offset(w * 0.868f, h * 0.34f)
            val pr = minOf(w * 0.06f, h * 0.1f)
            drawCircle(Color.Black.copy(alpha = 0.3f), pr, Offset(pc.x + 1.2f * u, pc.y + 1.8f * u))
            drawCircle(c(LATON), pr, pc)
            drawCircle(c(LATON_LUZ).copy(alpha = 0.6f), pr * 0.88f, pc, style = Stroke(0.7f * u))
            drawCircle(c(LATON_OSC), pr, pc, style = Stroke(0.8f * u))
            for (j in 0 until 6) {
                val g = Math.toRadians(30.0 + 60.0 * j).toFloat()
                drawCircle(c(LATON_OSC), pr * 0.09f, Offset(pc.x + pr * 0.84f * cos(g), pc.y + pr * 0.84f * sin(g)))
            }
            val vidrio = pr * 0.68f
            drawCircle(c(if (e.noche) AGUA_HONDA else AGUA), vidrio, pc)
            clipPath(Path().apply { addOval(Rect(pc, vidrio)) }) {
                listOf(-0.5f to -0.3f, 0.4f to 0.5f, 0.55f to -0.45f, -0.3f to 0.55f).forEach { (fx, fy) -> drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, Offset(pc.x + vidrio * fx, pc.y + vidrio * fy)) }
                medusa(Offset(pc.x, pc.y - vidrio * 0.1f), vidrio * 0.95f, if (e.noche) BIOLUZ else c(CASCO).copy(alpha = 0.8f), e.noche)
            }
            drawArc(Color.White.copy(alpha = 0.3f), 200f, 55f, false, Offset(pc.x - vidrio * 0.8f, pc.y - vidrio * 0.8f), Size(vidrio * 1.6f, vidrio * 1.6f), style = Stroke(1 * u))
            // La regla de profundidad: una raya cada tramo, más larga cada cinco, con sus metros.
            val xr = w * 0.835f
            for (k in 0..10) {
                val y = h * (0.48f + 0.028f * k)
                val largo = if (k % 5 == 0) w * 0.045f else w * 0.022f
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(xr, y), Offset(xr + largo, y), 0.8f * u)
                if (k % 5 == 0) textoEn(medidor, "${k * 2}", Rect(xr + w * 0.05f, y - 4 * u, cu.right - 2 * u, y + 4 * u), letra, c(TINTA).copy(alpha = 0.7f), 7f, minimo = 5f)
            }
            drawLine(c(TINTA).copy(alpha = 0.6f), Offset(xr, h * 0.48f), Offset(xr, h * 0.76f), 0.8f * u)
            // La bandera de buceo; de noche, una medusa luminosa.
            val bc = Offset(w * 0.868f, h * 0.85f)
            val bs = minOf(w * 0.045f, h * 0.05f)
            if (e.noche) {
                medusa(bc, bs * 2.2f, BIOLUZ, true)
            } else {
                val bandera = Rect(bc.x - bs, bc.y - bs * 0.75f, bc.x + bs, bc.y + bs * 0.75f)
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(bandera.left - 1.5f * u, bandera.top - 2 * u), Offset(bandera.left - 1.5f * u, bandera.bottom + 4 * u), 0.9f * u)
                drawRect(c(ROJO), bandera.topLeft, bandera.size)
                drawPath(Path().apply {
                    moveTo(bandera.left, bandera.top); lineTo(bandera.left + bandera.width * 0.22f, bandera.top)
                    lineTo(bandera.right, bandera.bottom); lineTo(bandera.right - bandera.width * 0.22f, bandera.bottom); close()
                }, c(Color.White))
            }
            if (a > 0f) drawRoundRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size, CornerRadius(10 * u))
        }
        // La pinza de metal arriba, con su rodillo, sus remaches y el ojal para colgarla.
        val pinza = Rect(w * 0.38f, h * 0.005f, w * 0.62f, h * 0.12f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), pinza.topLeft + Offset(1 * u, 2 * u), pinza.size, CornerRadius(3 * u))
        drawRoundRect(c(ALUMINIO), pinza.topLeft, pinza.size, CornerRadius(3 * u))
        drawRect(Color.White.copy(alpha = 0.35f), Offset(pinza.left + 3 * u, pinza.top + 1 * u), Size(pinza.width - 6 * u, 1.2f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(pinza.center.x - w * 0.03f, pinza.top + pinza.height * 0.25f), Size(w * 0.06f, pinza.height * 0.28f), CornerRadius(2 * u))
        val rodillo = Rect(w * 0.34f, h * 0.085f, w * 0.66f, h * 0.135f)
        drawRoundRect(c(ALUMINIO), rodillo.topLeft, rodillo.size, CornerRadius(rodillo.height / 2))
        drawLine(Color.White.copy(alpha = 0.45f), Offset(rodillo.left + 3 * u, rodillo.top + rodillo.height * 0.3f), Offset(rodillo.right - 3 * u, rodillo.top + rodillo.height * 0.3f), 0.9f * u)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(rodillo.left + 3 * u, rodillo.top + rodillo.height * 0.8f), Offset(rodillo.right - 3 * u, rodillo.top + rodillo.height * 0.8f), 0.9f * u)
        listOf(0.4f, 0.6f).forEach { f -> drawCircle(c(TINTA).copy(alpha = 0.5f), 1.5f * u, Offset(w * f, rodillo.center.y)) }
        // De noche, destellos de bioluminiscencia alrededor de la bitácora.
        if (e.noche) {
            listOf(0.04f to 0.05f, 0.22f to 0.04f, 0.8f to 0.05f, 0.985f to 0.4f, 0.975f to 0.78f, 0.01f to 0.6f, 0.5f to 0.985f).forEachIndexed { j, (fx, fy) ->
                destello(Offset(w * fx, h * fy), (0.9f + 0.6f * azar(e.semilla, 500 + j)) * u, if (j % 2 == 0) BIOLUZ else BIOLUZ_VERDE)
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Un perno de latón con su ranura. */
    private fun DrawScope.perno(o: Offset, r: Float, laton: Color, osc: Color, luz: Color) {
        val u = density
        drawCircle(osc, r, o)
        drawCircle(laton, r * 0.8f, o)
        drawCircle(luz, r * 0.3f, o - Offset(r * 0.25f, r * 0.25f))
        drawLine(osc, Offset(o.x - r * 0.55f, o.y + r * 0.2f), Offset(o.x + r * 0.55f, o.y - r * 0.2f), 0.6f * u)
    }

    /**
     * La cabecera de la hoja de inmersión, en un marco redondeado con un perno en cada esquina: a la izquierda, el
     * profundímetro de latón con la aguja según el día del año; a la derecha, «HOJA DE INMERSIÓN · COTA …», la regla de
     * profundidad, la fecha y el detalle, y unas burbujas que suben en la esquina. De noche, una medusa luminosa en lugar del
     * profundímetro, con plancton alrededor.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val marco = Rect(1 * u, 2 * u, w - 1 * u, h - 4 * u)
        drawRoundRect(p.acento.copy(alpha = 0.05f), marco.topLeft, marco.size, CornerRadius(6 * u))
        drawRoundRect(p.acento.copy(alpha = 0.5f), marco.topLeft, marco.size, CornerRadius(6 * u), style = Stroke(1.1f * u))
        listOf(Offset(marco.left + 6 * u, marco.top + 6 * u), Offset(marco.right - 6 * u, marco.top + 6 * u),
            Offset(marco.left + 6 * u, marco.bottom - 6 * u), Offset(marco.right - 6 * u, marco.bottom - 6 * u)).forEach { perno(it, 2.4f * u, c(LATON), c(LATON_OSC), c(LATON_LUZ)) }
        val centro = Offset(marco.left + 31 * u, marco.center.y)
        if (e.noche) {
            medusa(Offset(centro.x, centro.y - 4 * u), 40 * u, p.acento, true)
            for (k in 0 until 14) drawCircle(p.acento.copy(alpha = 0.2f + 0.5f * azar(e.numero, k)), (0.5f + 0.8f * azar(e.numero, k + 50)) * u,
                Offset(marco.left + 8 * u + 46 * u * azar(e.numero, k + 100), marco.top + 10 * u + (marco.height - 20 * u) * azar(e.numero, k + 200)))
        } else {
            drawCircle(Color.Black.copy(alpha = 0.15f), 23 * u, centro + Offset(0.8f * u, 1.4f * u))
            manometro(centro, 22 * u, c(LATON), c(Color(0xFFF2EEE2)), c(ROJO), 0.05f + 0.9f * ((e.numero % 366) / 366f))
            drawCircle(c(LATON_LUZ), 21.3f * u, centro, style = Stroke(0.8f * u))
            drawCircle(c(LATON_OSC), 17.6f * u, centro, style = Stroke(0.6f * u))
        }
        drawLine(p.acento.copy(alpha = 0.4f), Offset(marco.left + 62 * u, marco.top + 8 * u), Offset(marco.left + 62 * u, marco.bottom - 8 * u), 0.8f * u)
        val x0 = marco.left + 68 * u; val x1 = marco.right - 10 * u
        textoEn(medidor, e.rotulo, Rect(x0, marco.top + 5 * u, x1, marco.top + 21 * u), LetraExtra.batiscafo, p.acento, 12f, espaciado = 0.4f, minimo = 6.5f)
        // La regla de profundidad.
        val yr = marco.top + 25 * u
        drawLine(p.tenue.copy(alpha = 0.6f), Offset(x0 + 2 * u, yr), Offset(x1 - 2 * u, yr), 0.7f * u)
        var x = x0 + 2 * u; var k = 0
        while (x <= x1 - 2 * u) { drawLine(p.tenue.copy(alpha = 0.6f), Offset(x, yr), Offset(x, yr + if (k % 5 == 0) 4 * u else 2 * u), 0.6f * u); x += 6 * u; k++ }
        fechaCon(medidor, e.fecha, Rect(x0, marco.top + 31 * u, x1, marco.top + 75 * u), LetraExtra.atkinson, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, marco.top + 77 * u, x1, marco.top + 93 * u), LetraExtra.atkinson, p.tenue, 11f, minimo = 6.5f)
        // Burbujas que suben, en la esquina de abajo.
        listOf(Offset(-30f, -9f) to 2.6f, Offset(-24f, -15f) to 1.6f, Offset(-20f, -8f) to 1.2f, Offset(-16f, -13f) to 2f).forEach { (o, r) ->
            drawCircle(p.acento.copy(alpha = 0.45f), r * u, Offset(marco.right + o.x * u, marco.bottom + o.y * u), style = Stroke(0.7f * u))
        }
    }

    /**
     * El papel impermeable de la bitácora: la regla del profundímetro en el margen, más apretada cuanto más hondo, con una
     * marca de latón cada cinco rayas; una columna de burbujas que sube por la orilla derecha, un perno en cada esquina y el
     * sedimento ondulado del fondo al pie; de noche, plancton que brilla. En modo oscuro, la lámpara enjaulada de la cabina,
     * arriba al centro, con la sombra de los barrotes de su jaula.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        val x0 = 14 * u
        var y = 40 * u; var paso = 26 * u; var k = 0
        drawLine(p.acento.copy(alpha = 0.35f), Offset(x0, y), Offset(x0, h - 20 * u), 1 * u)
        while (y < h - 20 * u) {
            drawLine(p.acento.copy(alpha = 0.4f), Offset(x0, y), Offset(x0 + (if (k % 5 == 0) 9 else 4) * u, y), 1 * u)
            if (k % 5 == 0) drawCircle(c(LATON).copy(alpha = 0.6f), 1.4f * u, Offset(x0 - 4 * u, y))
            y += maxOf(paso, 1f); paso = maxOf(paso * 0.96f, 9 * u); k++
        }
        // La columna de burbujas por la orilla derecha.
        for (j in 0 until 18) {
            val by = h * (0.15f + 0.8f * azar(29, j))
            val bx = w - 12 * u + (azar(29, j + 40) - 0.5f) * 8 * u
            drawCircle(p.acento.copy(alpha = 0.16f), (1 + 3 * azar(29, j + 80)) * u, Offset(bx, by), style = Stroke(0.7f * u))
        }
        listOf(Offset(9 * u, 9 * u), Offset(w - 9 * u, 9 * u), Offset(9 * u, h - 9 * u), Offset(w - 9 * u, h - 9 * u)).forEach {
            perno(it, 3 * u, c(LATON).copy(alpha = 0.7f), c(LATON_OSC).copy(alpha = 0.7f), c(LATON_LUZ).copy(alpha = 0.7f))
        }
        // El sedimento del fondo.
        for (j in 0 until 3) {
            val onda = Path()
            val y0 = h - (16 + j * 7) * u
            onda.moveTo(20 * u, y0)
            var x = 20 * u
            while (x < w - 20 * u) { onda.quadraticTo(x + 9 * u, y0 - (2 + j) * u, x + 18 * u, y0); x += 18 * u }
            drawPath(onda, p.tenue.copy(alpha = 0.1f), style = Stroke(0.9f * u))
        }
        if (noche || p.oscura) {
            for (j in 0 until 90) drawCircle(p.acento.copy(alpha = 0.08f + 0.25f * azar(53, j)), (0.6f + 1.4f * azar(53, j + 100)) * u, Offset(w * azar(53, j + 200), h * azar(53, j + 300)))
        }
        if (oscuro && !p.oscura) {
            val lampara = Offset(w * 0.5f, -h * 0.01f)
            penumbraCon(lampara, LUZ_CABINA, 0.17f)
            // La sombra de los barrotes de la jaula de la lámpara.
            for (j in 0 until 6) hazCon(lampara, 45f + j * 18f, 2.2f, h * 1.1f, Color.Black.copy(alpha = 0.05f))
        }
    }

    /** La barandilla de latón al pie de la barra, con sus postes y sus bridas. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        var x = 30 * u
        while (x < w) {
            drawRect(c(LATON_OSC), Offset(x - 1.2f * u, h - 6 * u), Size(2.4f * u, 6 * u))
            drawRect(c(LATON), Offset(x - 2.4f * u, h - 1.6f * u), Size(4.8f * u, 1.6f * u))
            x += 64 * u
        }
        val r = Rect(0f, h - 10 * u, w, h - 5.5f * u)
        drawRect(Brush.verticalGradient(listOf(c(LATON_LUZ), c(LATON), c(LATON_OSC)), r.top, r.bottom), r.topLeft, r.size)
    }

    /** Una placa de goma amarilla de bitácora, con un remache de aluminio en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(1 * u, 2 * u), size, CornerRadius(9 * u))
        drawRoundRect(c(GOMAS[0]), Offset.Zero, size, CornerRadius(9 * u))
        drawRoundRect(c(mezcla(GOMAS[0], GOMA, 0.35f)), Offset(0.6f * u, 0.6f * u), Size(w - 1.2f * u, h - 1.2f * u), CornerRadius(8.5f * u), style = Stroke(1 * u))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(9 * u, 2.2f * u), Offset(w - 9 * u, 2.2f * u), 1 * u)
        listOf(Offset(7.5f * u, h / 2), Offset(w - 7.5f * u, h / 2)).forEach { o ->
            drawCircle(c(mezcla(ALUMINIO, Color.Black, 0.35f)), 2.6f * u, o)
            drawCircle(c(ALUMINIO), 2 * u, o)
            drawLine(c(mezcla(ALUMINIO, Color.Black, 0.45f)), Offset(o.x - 1.3f * u, o.y), Offset(o.x + 1.3f * u, o.y), 0.6f * u)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFF14181A)

    // ---------------------------------------------------------------- la libreta de momentos

    /** La lona engomada azul petróleo de la bitácora, su crema estampado, y la cadena. */
    private val LONA = Color(0xFF1F4A59)
    private val CREMA_LONA = Color(0xFFE6DFC6)
    private val CADENA = Color(0xFF8E9296)

    override val altoLibreta: Dp = 258.dp

    /**
     * La bitácora del batiscafo sobre la mesa abatible de la cabina: el tablero de teca colgado del casco con sus dos cadenas
     * y sus bisagras de latón, contra las planchas color crema con sus costuras remachadas. Arriba a la derecha, el ojo de
     * buey de latón con sus pernos, por el que se ve el agua honda, la nieve marina y una medusa; arriba a la izquierda, la
     * lámpara enjaulada. En la mesa, el cronómetro de buceo, el lápiz atado con su cordel y la linterna. La bitácora es de lona
     * engomada azul petróleo con cantoneras de latón, la regla de profundidad en el lomo, el batiscafo estampado en crema y la
     * placa de latón con el nombre y la cuenta; engorda con las hojas y por abajo asoman las fichas de pizarra blanca de cada
     * inmersión (una cada 15 hojas, hasta 8). Al abrirse, la correa de goma se suelta y la tapa gira sobre el lomo: dentro, la
     * hoja con el perfil de la inmersión. En modo oscuro, la cabina de noche: alumbra la lámpara enjaulada (un degradado) y,
     * por el ojo de buey, la medusa (el otro).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (suelta, abre) = tiemposLibreta(e.abierto)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF05080B), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF05080B), 0.3f) else x
        // El casco: planchas crema con sus costuras remachadas.
        drawRect(c(CASCO))
        listOf(0.33f, 0.72f).forEach { f ->
            val y = h * f
            drawLine(c(CASCO_SOMBRA), Offset(0f, y), Offset(w, y), 2 * u)
            var x = 6 * u
            while (x < w) { drawCircle(c(REMACHE), 1.8f * u, Offset(x, y - 5 * u)); drawCircle(c(REMACHE), 1.8f * u, Offset(x, y + 5 * u)); x += 13 * u }
        }
        listOf(0.42f).forEach { f -> drawLine(c(CASCO_SOMBRA), Offset(w * f, 0f), Offset(w * f, h * 0.33f), 2 * u) }
        // El ojo de buey: el agua honda, la nieve marina y una medusa.
        val ob = Offset(w * 0.86f, h * 0.2f); val rb = 30 * u
        drawCircle(c(LATON_OSC), rb + 8 * u, ob)
        drawCircle(c(LATON), rb + 6 * u, ob)
        drawCircle(c(LATON_LUZ), rb + 6 * u, ob, style = Stroke(1 * u))
        for (k in 0 until 8) { val an = k * PI / 4; drawCircle(c(LATON_OSC), 2 * u, Offset(ob.x + (rb + 3 * u) * cos(an).toFloat(), ob.y + (rb + 3 * u) * sin(an).toFloat())) }
        drawCircle(AGUA_HONDA, rb, ob)
        drawCircle(Brush.radialGradient(listOf(BIOLUZ.copy(alpha = if (oscuro) 0.45f else 0.3f), Color.Transparent), Offset(ob.x - 6 * u, ob.y - 2 * u), rb), rb, ob)
        for (k in 0 until 10) drawCircle(Color.White.copy(alpha = 0.5f), (0.5f + 0.8f * azar(180, k)) * u, Offset(ob.x - rb * 0.8f + rb * 1.6f * azar(181, k), ob.y - rb * 0.6f + rb * 1.3f * azar(182, k)))
        val me = Offset(ob.x - 6 * u, ob.y - 6 * u)
        drawArc(BIOLUZ.copy(alpha = 0.85f), 180f, 180f, true, Offset(me.x - 9 * u, me.y - 8 * u), Size(18 * u, 16 * u))
        for (k in 0 until 4) drawPath(Path().apply { moveTo(me.x - 6 * u + k * 4 * u, me.y); quadraticTo(me.x - 8 * u + k * 4 * u, me.y + 8 * u, me.x - 5 * u + k * 4 * u, me.y + 16 * u) },
            BIOLUZ.copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawArc(Color.White.copy(alpha = 0.18f), 200f, 70f, false, Offset(ob.x - rb * 0.8f, ob.y - rb * 0.8f), Size(rb * 1.6f, rb * 1.6f), style = Stroke(3 * u))
        // La lámpara enjaulada, arriba a la izquierda.
        val la = Offset(w * 0.1f, h * 0.15f)
        drawRect(c(LATON_OSC), Offset(la.x - 12 * u, la.y - 18 * u), Size(24 * u, 5 * u))
        drawOval(if (oscuro) LUZ_CABINA else c(LUZ_CABINA).copy(alpha = 0.7f), Offset(la.x - 9 * u, la.y - 13 * u), Size(18 * u, 26 * u))
        listOf(-9f, -3f, 3f, 9f).forEach { dx -> drawLine(c(LATON_OSC), Offset(la.x + dx * u, la.y - 13 * u), Offset(la.x + dx * 0.6f * u, la.y + 13 * u), 1.2f * u) }
        listOf(-4f, 5f).forEach { dy -> drawLine(c(LATON_OSC), Offset(la.x - 9.5f * u, la.y + dy * u), Offset(la.x + 9.5f * u, la.y + dy * u), 1.2f * u) }
        if (oscuro) drawRect(Brush.radialGradient(listOf(LUZ_CABINA.copy(alpha = 0.34f), LUZ_CABINA.copy(alpha = 0.1f), Color.Transparent), la, w * 0.85f))
        // La mesa abatible: las bisagras en el casco, las cadenas y el tablero de teca con su canto.
        val mesa = Rect(w * 0.02f, h * 0.52f, w * 0.98f, h * 0.88f)
        listOf(mesa.left + 4 * u to mesa.left + 40 * u, mesa.right - 4 * u to mesa.right - 40 * u).forEach { (x0, x1) ->
            val p = Offset(x1, h * 0.36f); val q = Offset(x0, mesa.bottom - 4 * u)
            for (k in 0 until 14) {
                val f = (k + 0.5f) / 14f
                val m = Offset(p.x + (q.x - p.x) * f, p.y + (q.y - p.y) * f)
                drawOval(c(CADENA), Offset(m.x - 2 * u, m.y - 3 * u), Size(4 * u, 6 * u), style = Stroke(1.1f * u))
            }
            drawCircle(c(LATON), 3 * u, Offset(x1, h * 0.36f))
        }
        drawRect(c(TECA), mesa.topLeft, mesa.size)
        vetas(mesa, c(TECA_VETA).copy(alpha = 0.6f), 183, densidad = 0.9f)
        var tx = mesa.left + 30 * u
        while (tx < mesa.right) { drawLine(c(TECA_VETA).copy(alpha = 0.7f), Offset(tx, mesa.top), Offset(tx, mesa.bottom), 1.2f * u); tx += 46 * u }
        drawRect(c(TECA_LUZ), Offset(mesa.left, mesa.bottom), Size(mesa.width, 8 * u))
        drawRect(c(LATON), Offset(mesa.left, mesa.bottom + 8 * u), Size(mesa.width, 2 * u))
        listOf(0.2f, 0.8f).forEach { f -> drawRect(c(LATON), Offset(mesa.left + mesa.width * f - 12 * u, mesa.top - 3 * u), Size(24 * u, 5 * u)) }
        // El cronómetro de buceo, la linterna y el lápiz atado.
        val cr = Offset(w * 0.1f, h * 0.7f)
        drawCircle(Color.Black.copy(alpha = 0.25f), 15 * u, cr + Offset(2 * u, 3 * u))
        drawRect(c(GOMA), Offset(cr.x - 5 * u, cr.y - 24 * u), Size(10 * u, 48 * u))
        drawCircle(c(ALUMINIO), 14 * u, cr)
        drawCircle(c(TINTA), 11 * u, cr)
        for (k in 0 until 12) { val an = k * PI / 6; drawLine(c(BIOLUZ_VERDE), Offset(cr.x + 8 * u * cos(an).toFloat(), cr.y + 8 * u * sin(an).toFloat()), Offset(cr.x + 10 * u * cos(an).toFloat(), cr.y + 10 * u * sin(an).toFloat()), 1 * u) }
        drawLine(c(ALUMINIO), cr, cr + Offset(5 * u, -5 * u), 1.2f * u)
        drawArc(c(ROJO), -90f, 80f, false, Offset(cr.x - 12.5f * u, cr.y - 12.5f * u), Size(25 * u, 25 * u), style = Stroke(2 * u))
        rotate(-10f, Offset(w * 0.88f, h * 0.78f)) {
            val o = Offset(w * 0.88f - 22 * u, h * 0.78f - 6 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), o + Offset(2 * u, 3 * u), Size(44 * u, 12 * u))
            drawRoundRect(c(GOMAS[1]), o, Size(34 * u, 12 * u), CornerRadius(3 * u))
            drawRect(c(GOMA), o + Offset(30 * u, -2 * u), Size(14 * u, 16 * u))
            drawOval(c(LUZ_CABINA).copy(alpha = 0.8f), o + Offset(40 * u, 0f), Size(5 * u, 12 * u))
        }
        drawPath(Path().apply { moveTo(w * 0.8f, h * 0.6f); quadraticTo(w * 0.86f, h * 0.68f, w * 0.81f, h * 0.7f) }, c(CREMA_LONA), style = Stroke(0.8f * u))
        rotate(30f, Offset(w * 0.8f, h * 0.7f)) { drawRect(c(GOMAS[0]), Offset(w * 0.8f, h * 0.7f - 2 * u), Size(30 * u, 4 * u)); drawRect(c(TINTA), Offset(w * 0.8f + 30 * u, h * 0.7f - 1 * u), Size(4 * u, 2 * u)) }
        // La bitácora.
        val ancho = w * 0.56f; val alto = minOf(ancho * 0.62f, h * 0.48f)
        val r = Rect(w * 0.2f, h * 0.36f, w * 0.2f + ancho, h * 0.36f + alto)
        val g = (3f + 12f * lleno) * u
        val lomo = r.left + ancho * 0.11f
        rotate(-2.5f, r.center) {
            drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), r.topLeft + Offset(5 * u, 5 * u + g), r.size, CornerRadius(4 * u))
            // Las fichas de pizarra de cada inmersión, que asoman por abajo.
            val n = (1 + e.hojas / 15).coerceAtMost(8)
            for (k in 0 until n) {
                val fx = r.left + ancho * (0.18f + 0.095f * k)
                val sale = (8f + 8f * azar(e.semilla, 190 + k)) * u
                rotate(-6f + 12f * azar(e.semilla, 200 + k), Offset(fx + 10 * u, r.bottom)) {
                    drawRoundRect(c(PIZARRA), Offset(fx, r.bottom + g - 6 * u), Size(20 * u, sale + 6 * u), CornerRadius(2 * u))
                    drawCircle(c(CASCO_SOMBRA), 1.6f * u, Offset(fx + 10 * u, r.bottom + g + sale - 4 * u))
                    drawLine(c(TINTA).copy(alpha = 0.4f), Offset(fx + 4 * u, r.bottom + g + 2 * u), Offset(fx + 14 * u, r.bottom + g + 2 * u), 0.7f * u)
                }
            }
            drawRoundRect(c(mezcla(LONA, Color.Black, 0.35f)), r.topLeft + Offset(0f, g), r.size, CornerRadius(4 * u))
            cantoLibreta(r, g * 0.85f, c(PIZARRA), c(CASCO_SOMBRA))
            // Por dentro: la hoja con el perfil de la inmersión.
            if (abre > 0.01f) {
                val hoja = Rect(lomo, r.top, r.right, r.bottom)
                drawRect(cp(PIZARRA), hoja.topLeft, hoja.size)
                val gr = Rect(hoja.left + 14 * u, hoja.top + 22 * u, hoja.right - 12 * u, hoja.bottom - 12 * u)
                textoEn(medidor, "PERFIL DE INMERSIÓN", Rect(gr.left, hoja.top + 6 * u, gr.right, hoja.top + 18 * u), letra, c(TINTA), 9f, peso = FontWeight.Bold, espaciado = 1.5f)
                var gx = gr.left
                while (gx <= gr.right) { drawLine(c(LONA).copy(alpha = 0.25f), Offset(gx, gr.top), Offset(gx, gr.bottom), 0.5f * u); gx += 10 * u }
                var gy = gr.top
                while (gy <= gr.bottom) { drawLine(c(LONA).copy(alpha = 0.25f), Offset(gr.left, gy), Offset(gr.right, gy), 0.5f * u); gy += 10 * u }
                drawPath(Path().apply { moveTo(gr.left, gr.top + 2 * u); lineTo(gr.left + gr.width * 0.18f, gr.bottom - 8 * u); lineTo(gr.left + gr.width * 0.62f, gr.bottom - 4 * u)
                    lineTo(gr.left + gr.width * 0.74f, gr.top + gr.height * 0.4f); lineTo(gr.left + gr.width * 0.84f, gr.top + gr.height * 0.4f); lineTo(gr.right, gr.top + 2 * u) },
                    c(ROJO), style = Stroke(1.4f * u))
            }
            // La tapa de lona, que gira sobre el lomo.
            val sx = 1f - abre
            if (sx > 0.02f) scale(sx, 1f, Offset(lomo, r.center.y)) {
                drawRoundRect(c(LONA), r.topLeft, r.size, CornerRadius(4 * u))
                var ty = r.top
                while (ty < r.bottom) { drawLine(Color.Black.copy(alpha = 0.07f), Offset(r.left, ty), Offset(r.right, ty), 0.6f * u); ty += 3 * u }
                listOf(Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (p, s) ->
                    val l = ancho * 0.11f
                    drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x + s.x * l, p.y); lineTo(p.x, p.y + s.y * l); close() }, c(LATON))
                    drawCircle(c(LATON_OSC), 1.5f * u, Offset(p.x + s.x * l * 0.3f, p.y + s.y * l * 0.3f))
                }
                // El batiscafo estampado en crema: el flotador, la torreta, la esfera y su ojo de buey.
                val tinta = c(CREMA_LONA).copy(alpha = 0.8f)
                val bc = Offset(r.left + ancho * 0.58f, r.top + alto * 0.26f); val L = ancho * 0.26f
                drawRoundRect(tinta, Offset(bc.x - L, bc.y - L * 0.14f), Size(L * 2, L * 0.28f), CornerRadius(L * 0.14f), style = Stroke(1.3f * u))
                drawRect(tinta, Offset(bc.x - L * 0.1f, bc.y - L * 0.3f), Size(L * 0.2f, L * 0.16f), style = Stroke(1.1f * u))
                drawLine(tinta, Offset(bc.x, bc.y + L * 0.14f), Offset(bc.x, bc.y + L * 0.22f), 1.1f * u)
                drawCircle(tinta, L * 0.16f, Offset(bc.x, bc.y + L * 0.38f), style = Stroke(1.3f * u))
                drawCircle(tinta, L * 0.06f, Offset(bc.x - L * 0.04f, bc.y + L * 0.4f))
                // La placa de latón con el nombre y la cuenta.
                val pl = Rect(r.left + ancho * 0.17f, r.top + alto * 0.52f, r.left + ancho * 0.82f, r.top + alto * 0.88f)
                drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(1.2f * u, 1.6f * u), pl.size, CornerRadius(3 * u))
                drawRoundRect(cp(LATON), pl.topLeft, pl.size, CornerRadius(3 * u))
                drawRoundRect(cp(LATON_LUZ), pl.topLeft, Size(pl.width, 2 * u), CornerRadius(1 * u))
                drawRoundRect(c(LATON_OSC), pl.topLeft, pl.size, CornerRadius(3 * u), style = Stroke(1 * u))
                listOf(Offset(pl.left + 5 * u, pl.top + 5 * u), Offset(pl.right - 5 * u, pl.top + 5 * u), Offset(pl.left + 5 * u, pl.bottom - 5 * u), Offset(pl.right - 5 * u, pl.bottom - 5 * u))
                    .forEach { drawCircle(c(LATON_OSC), 1.5f * u, it) }
                textoEn(medidor, e.nombre, Rect(pl.left + 10 * u, pl.top + 3 * u, pl.right - 10 * u, pl.top + pl.height * 0.58f), letra, c(TINTA), 13f, peso = FontWeight.Bold, minimo = 7f)
                textoEn(medidor, e.cuenta, Rect(pl.left + 10 * u, pl.top + pl.height * 0.56f, pl.right - 10 * u, pl.bottom - 3 * u), letra, c(TINTA).copy(alpha = 0.8f), 9f, minimo = 6f)
                if (abre > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * abre), r.topLeft, r.size, CornerRadius(4 * u))
            }
            if (abre > 0.01f && sx > 0.02f) drawRect(Color.Black.copy(alpha = 0.16f), Offset(lomo + (r.right - lomo) * sx, r.top), Size(14 * u * sin(PI.toFloat() * abre), r.height))
            // El lomo con la regla de profundidad: una raya cada 100 m y una más larga cada 500.
            drawRoundRect(c(mezcla(LONA, Color.Black, 0.3f)), r.topLeft, Size(lomo - r.left, r.height), CornerRadius(4 * u))
            var k = 0; var y = r.top + 5 * u
            while (y < r.bottom - 4 * u) { drawLine(c(CREMA_LONA).copy(alpha = 0.65f), Offset(r.left, y), Offset(r.left + (lomo - r.left) * (if (k % 5 == 0) 0.75f else 0.4f), y), 0.9f * u); y += 4.5f * u; k++ }
            // La correa de goma negra, que al abrir se suelta.
            val queda = 1f - suelta
            if (queda > 0f) {
                val xl = r.left + ancho * 0.86f + suelta * ancho * 0.18f
                drawRect(Color.Black.copy(alpha = 0.3f * queda), Offset(xl + 2 * u, r.top), Size(7 * u, r.height + g))
                drawRect(c(GOMA).copy(alpha = queda), Offset(xl, r.top - 2 * u), Size(7 * u, r.height + g + 3 * u))
                drawLine(Color.White.copy(alpha = 0.12f * queda), Offset(xl + 1.5f * u, r.top), Offset(xl + 1.5f * u, r.bottom + g), 0.8f * u)
            }
        }
    }

    /**
     * La pestaña del día es una ficha de goma amarilla de las que cuelgan del cinturón, con su ojal de latón arriba, la regla
     * de profundidad en el canto izquierdo, la letra de la semana, el día grande y la luz del ánimo abajo, en su color, con su
     * halo (sin ánimo, la luz apagada).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size, CornerRadius(5 * u))
        drawRoundRect(c(GOMAS[(e.semilla and 0x7fffffff) % GOMAS.size]), r.topLeft, r.size, CornerRadius(5 * u))
        drawRoundRect(c(TINTA).copy(alpha = 0.35f), r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(0.8f * u))
        val ojal = Offset(r.center.x, r.top + 5.5f * u)
        drawCircle(c(LATON), 3 * u, ojal); drawCircle(c(TINTA), 1.5f * u, ojal)
        var y = r.top + 12 * u; var k = 0
        while (y < r.bottom - 4 * u) { drawLine(c(TINTA).copy(alpha = 0.5f), Offset(r.left, y), Offset(r.left + (if (k % 5 == 0) 5 else 3) * u, y), 0.7f * u); y += 2.5f * u; k++ }
        textoEn(medidor, e.semana, Rect(r.right - 11 * u, r.top + 2 * u, r.right - 2 * u, r.top + 10 * u), letra, c(TINTA).copy(alpha = 0.75f), 7.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 5 * u, r.top + 11 * u, r.right - 2 * u, r.top + 33 * u), letra, c(TINTA), 18f, peso = FontWeight.Bold, minimo = 9f)
        val luz = Offset(r.center.x + 1.5f * u, r.bottom - 9 * u)
        drawCircle(c(GOMA), 6.5f * u, luz)
        if (e.tono != null) { drawCircle(e.tono, 5 * u, luz); drawCircle(Color.White.copy(alpha = 0.55f), 1.5f * u, luz + Offset(-1.6f * u, -1.6f * u)) }
        else drawCircle(c(ALUMINIO).copy(alpha = 0.5f), 4.5f * u, luz, style = Stroke(0.8f * u))
    }

    /** El mes en cinta de rotular negra, de letras blancas en relieve, pegada a una plancha del casco con sus remaches. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(CASCO))
        drawLine(c(CASCO_SOMBRA), Offset(0f, 2 * u), Offset(w, 2 * u), 1.4f * u)
        drawLine(c(CASCO_SOMBRA), Offset(0f, h - 2 * u), Offset(w, h - 2 * u), 1.4f * u)
        var x = 7 * u
        while (x < w) { drawCircle(c(REMACHE), 1.4f * u, Offset(x, 5.5f * u)); drawCircle(c(REMACHE), 1.4f * u, Offset(x, h - 5.5f * u)); x += 14 * u }
        val medida = medidor.measure(texto.uppercase(), TextStyle(fontFamily = letra, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)).size.width
        val ancho = (medida + 30 * u).coerceIn(w * 0.35f, w - 20 * u)
        val ci = Rect(w / 2 - ancho / 2, 9.5f * u, w / 2 + ancho / 2, h - 9.5f * u)
        rotate(-0.8f, ci.center) {
            drawRect(Color.Black.copy(alpha = 0.3f), ci.topLeft + Offset(0.8f * u, 1.2f * u), ci.size)
            drawRect(c(CINTA), ci.topLeft, ci.size)
            drawLine(Color.White.copy(alpha = 0.1f), Offset(ci.left, ci.top + 1 * u), Offset(ci.right, ci.top + 1 * u), 0.8f * u)
            textoEn(medidor, texto.uppercase(), Rect(ci.left + 10 * u, ci.top + 1 * u, ci.right - 10 * u, ci.bottom - 1 * u), letra, Color.White.copy(alpha = 0.92f), 12f,
                peso = FontWeight.Bold, espaciado = 2f, minimo = 6.5f)
        }
    }

    // ---------------------------------------------------------------- las cartas

    /** El esmalte verde petróleo de la caja estanca, su sombra, el cabo de cáñamo, el plomo de lastre y el vidrio de las botellas. */
    private val ESMALTE = Color(0xFF2C5A6B)
    private val ESMALTE_OSC = Color(0xFF15333F)
    private val ESMALTE_LUZ = Color(0xFF4D8597)
    private val CABO = Color(0xFFCDB88C)
    private val PLOMO = Color(0xFF6E7378)
    private val VIDRIO_BOTELLA = Color(0xFF5E9C8E)
    private val CORCHO = Color(0xFFB48A5A)
    private val ARENA = Color(0xFFB9A57E)
    private val BOYA = Color(0xFFE8792A)

    /** Una tira de cinta de rotular con [texto] en relieve: plástico oscuro, letras blancas. */
    private fun DrawScope.cintaRotular(r: Rect, texto: String, medidor: TextMeasurer, tam: Float, oscuro: Boolean) {
        val u = density
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(0.6f * u, 1 * u), r.size)
        drawRect(t(CINTA, oscuro, false), r.topLeft, r.size)
        drawRect(Color.White.copy(alpha = 0.08f), r.topLeft, Size(r.width, r.height * 0.3f))
        textoEn(medidor, texto, Rect(r.left + 3 * u, r.top, r.right - 3 * u, r.bottom), letra, Color(0xFFF1F3F2), tam, peso = FontWeight.Bold, minimo = 6f)
    }

    /** El sello de latón estampado con la escafandra: el casco redondo, la mirilla y sus tres portillos. */
    private fun DrawScope.selloEscafandra(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        drawCircle(Color.Black.copy(alpha = 0.35f * alfa), r, c + Offset(r * 0.08f, r * 0.12f))
        drawCircle(k(LATON), r, c)
        drawCircle(k(LATON_OSC), r * 0.86f, c, style = Stroke(r * 0.08f))
        for (i in 0 until 12) { val a = (2 * PI * i / 12).toFloat(); drawCircle(k(LATON_OSC), r * 0.05f, Offset(c.x + cos(a) * r * 0.72f, c.y + sin(a) * r * 0.72f)) }
        drawCircle(k(LATON_LUZ), r * 0.42f, Offset(c.x, c.y - r * 0.05f))
        drawRect(k(LATON_LUZ), Offset(c.x - r * 0.38f, c.y + r * 0.25f), Size(r * 0.76f, r * 0.18f))
        drawCircle(k(LATON_OSC), r * 0.2f, Offset(c.x, c.y - r * 0.05f))
        listOf(-1f, 1f).forEach { s -> drawCircle(k(LATON_OSC), r * 0.08f, Offset(c.x + s * r * 0.3f, c.y - r * 0.08f)) }
        drawCircle(k(LATON_OSC), r * 0.08f, Offset(c.x, c.y - r * 0.36f))
        drawArc(k(Color.White.copy(alpha = 0.4f)), 200f, 60f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /**
     * La caja estanca de las cartas del batiscafo: acero esmaltado verde petróleo con su marco de latón y sus pernos, la tapa
     * con la junta de goma, dos cierres de palanca a los lados y un ojo de buey en la tapa por donde se ve una medusa del
     * fondo; en el frente, a quién va y las fechas en dos tiras de cinta de rotular de letras blancas en relieve. La cierra
     * el sello de latón con la escafandra; las de al futuro van además lastradas: dos vueltas de cabo de cáñamo y el plomo
     * de buceo colgando del nudo. Al abrirla, el sello se parte, los cierres saltan, la tapa se levanta mostrando su junta
     * y sube la carta, la hoja de la bitácora. En modo oscuro, todo se hunde en el azul del fondo y el sello que ya se puede
     * abrir brilla con luz de bioluminiscencia.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 3 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 3 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        val rad = CornerRadius(5 * u)
        val borde = top + H * 0.14f
        val tapaH = H * 0.32f
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val junta = borde + tapaH * 0.56f
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(l + 2 * u, borde + 3 * u), Size(W, bot - borde), rad)
        // Por dentro: el hueco, con la junta de goma.
        drawRoundRect(c(ESMALTE_OSC), Offset(l, borde - 3 * u), Size(W, H * 0.3f), rad)
        drawRect(c(GOMA), Offset(l + 4 * u, borde - 2 * u), Size(W - 8 * u, 2.5f * u))
        // La tapa levantada, vista por dentro.
        if (k < 0f) {
            val alto = tapaH * -k
            val tr = Rect(l, borde - alto, r, borde)
            drawRoundRect(c(ESMALTE_OSC), tr.topLeft, tr.size, CornerRadius(4 * u))
            drawRoundRect(c(GOMA), tr.deflate(3 * u).topLeft, tr.deflate(3 * u).size, CornerRadius(3 * u), style = Stroke(2 * u))
            drawRect(c(LATON_OSC), Offset(l, borde - 2 * u), Size(W, 2 * u))
        }
        // La hoja de la bitácora.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.8f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 9 * u, 5.5f * u, 7 * u, oscuro)
            drawRect(c(AGUA).copy(alpha = 0.7f), Offset(h.left + 4 * u, h.top + 3 * u), Size(h.width * 0.3f, 1.6f * u))
            drawLine(c(ROJO).copy(alpha = 0.5f), Offset(h.left + 5 * u, h.top), Offset(h.left + 5 * u, h.bottom), 0.6f * u)
        }
        // El cuerpo de la caja.
        val caja = Rect(l, borde, r, bot)
        drawRoundRect(c(ESMALTE), caja.topLeft, caja.size, rad)
        drawRoundRect(c(LATON), caja.topLeft, caja.size, rad, style = Stroke(2.2f * u))
        drawRoundRect(c(ESMALTE_LUZ).copy(alpha = 0.5f), caja.deflate(3.5f * u).topLeft, caja.deflate(3.5f * u).size, CornerRadius(3 * u), style = Stroke(0.6f * u))
        listOf(Offset(l + 6 * u, borde + 6 * u), Offset(r - 6 * u, borde + 6 * u), Offset(l + 6 * u, bot - 6 * u), Offset(r - 6 * u, bot - 6 * u)).forEach {
            perno(it, 2 * u, c(LATON), c(LATON_OSC), c(LATON_LUZ))
        }
        // Las dos tiras de cinta de rotular.
        val t1 = Rect(l + W * 0.08f, top + H * 0.6f, r - W * 0.08f, top + H * 0.76f)
        val t2 = Rect(l + W * 0.16f, top + H * 0.79f, r - W * 0.16f, top + H * 0.9f)
        if (e.linea1.isNotBlank()) cintaRotular(t1, e.linea1.uppercase(), medidor, 10.5f, oscuro)
        if (e.linea2.isNotBlank()) cintaRotular(t2, e.linea2, medidor, 9f, oscuro)
        if (e.linea1.isBlank() && e.linea2.isBlank()) {
            // Sin renglones (al abrir o sellar): la placa de profundidad.
            val pl = Rect(cx - W * 0.2f, top + H * 0.66f, cx + W * 0.2f, top + H * 0.84f)
            drawRoundRect(c(LATON), pl.topLeft, pl.size, CornerRadius(2 * u))
            textoEn(medidor, "ESTANCA · 300 m", pl.deflate(2 * u), letra, c(TINTA), 9f, peso = FontWeight.Bold, minimo = 4f)
        }
        // La tapa cerrada (o cerrándose), con su ojo de buey.
        if (k >= 0f) {
            val alto = tapaH * k
            val tr = Rect(l - 1 * u, borde - alto * 0.44f, r + 1 * u, borde - alto * 0.44f + alto)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), tr.topLeft + Offset(0f, 2 * u), tr.size, CornerRadius(5 * u))
            drawRoundRect(c(ESMALTE), tr.topLeft, tr.size, CornerRadius(5 * u))
            if (k > 0.2f) scale(1f, k, Offset(cx, tr.top)) {
                val tc = Rect(tr.left, tr.top, tr.right, tr.top + tapaH)
                drawRoundRect(c(LATON), tc.topLeft, tc.size, CornerRadius(5 * u), style = Stroke(2.2f * u))
                drawRect(c(GOMA), Offset(tc.left + 2 * u, tc.bottom - 3 * u), Size(tc.width - 4 * u, 2 * u))
                val ob = Offset(r - W * 0.17f, tc.top + tapaH * 0.48f); val orr = tapaH * 0.34f
                drawCircle(c(LATON_OSC), orr + 2.5f * u, ob)
                drawCircle(c(LATON), orr + 1.5f * u, ob)
                drawCircle(t(AGUA_HONDA, oscuro, false), orr, ob)
                medusa(ob + Offset(0f, orr * 0.1f), orr * 1.1f, BIOLUZ.copy(alpha = 0.85f), true)
                drawArc(Color.White.copy(alpha = 0.25f), 200f, 70f, false, Offset(ob.x - orr * 0.8f, ob.y - orr * 0.8f), Size(orr * 1.6f, orr * 1.6f), style = Stroke(1 * u))
                for (i in 0 until 6) { val a = (2 * PI * i / 6).toFloat(); drawCircle(c(LATON_OSC), 0.9f * u, Offset(ob.x + cos(a) * (orr + 1.6f * u), ob.y + sin(a) * (orr + 1.6f * u))) }
                textoEn(medidor, "Nº ${(e.semilla and 0x7fffffff) % 900 + 100}", Rect(l + W * 0.2f, tc.top + tapaH * 0.25f, l + W * 0.45f, tc.top + tapaH * 0.7f), letra, c(LATON_LUZ), 8f, peso = FontWeight.Bold, minimo = 4f)
            }
        }
        // Los cierres de palanca, a los lados.
        val salto = (e.romper * 1.2f).coerceIn(0f, 1f).coerceAtLeast(e.abrir)
        listOf(l + W * 0.1f, r - W * 0.1f).forEach { px ->
            val base = Offset(px, junta + 5 * u)
            drawRoundRect(c(LATON_OSC), Offset(px - 4 * u, base.y - 2 * u), Size(8 * u, 8 * u), CornerRadius(1.5f * u))
            rotate(-150f * salto * (if (px < cx) 1f else -1f), base) {
                drawRoundRect(c(LATON), Offset(px - 2.2f * u, base.y - 13 * u), Size(4.4f * u, 14 * u), CornerRadius(2 * u))
                drawRoundRect(c(LATON_LUZ), Offset(px - 1.2f * u, base.y - 12 * u), Size(1 * u, 11 * u), CornerRadius(0.5f * u))
            }
            drawCircle(c(LATON_LUZ), 1.4f * u, base)
        }
        // El cabo de lastre y el plomo.
        val y0 = junta
        if (e.cordon > 0f) {
            listOf(-1.8f * u, 1.8f * u).forEachIndexed { i, dy ->
                val p = Path().apply { moveTo(l - 2 * u, y0 + dy); lineTo(r + 2 * u, y0 + dy + 0.5f * u) }
                val f = (e.cordon * 1.3f - i * 0.15f).coerceIn(0f, 1f)
                trazoParcialCo(p, f, c(CABO), 2.2f * u, StrokeCap.Butt)
                trazoParcialCo(p, f, c(mezcla(CABO, Color.Black, 0.3f)), 0.5f * u, StrokeCap.Butt)
            }
            val cae = ((e.cordon - 0.75f) / 0.25f).coerceIn(0f, 1f)
            if (cae > 0f) {
                val n0 = Offset(l + W * 0.28f, y0)
                drawCircle(c(CABO), 3 * u, n0)
                val pb = Offset(n0.x, n0.y + (6 + 10 * cae) * u)
                drawLine(c(CABO), n0, pb, 1.4f * u)
                drawRoundRect(c(PLOMO), Offset(pb.x - 5 * u, pb.y), Size(10 * u, 7 * u), CornerRadius(2 * u))
                drawRect(Color.White.copy(alpha = 0.2f), Offset(pb.x - 4 * u, pb.y + 1 * u), Size(8 * u, 1 * u))
            }
        }
        // El sello de latón con la escafandra, en la junta.
        val selloR = H * 0.13f
        val sc = Offset(cx, junta)
        val subeTapa = if (k >= 0f) 0f else H * 0.5f
        if (e.alfaSello > 0f) {
            if (k >= 0f) brilloSelloCo(sc, selloR, BIOLUZ, e.brillo)
            selloPartidoCo(sc, selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, -selloR * 0.4f * e.romper - subeTapa),
                Offset(selloR * 0.25f * e.romper, selloR * 0.3f * e.romper), e.semilla, conArriba = k >= 0f) { selloEscafandra(sc, selloR, oscuro, e.alfaSello) }
        }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.235f, 0.83f, 0.39f)

    override val altoBuzon: Dp = 210.dp

    /** Una botella con su mensaje, tumbada en la red: lista (brilla), sellada (corcho con lacre y su hilo) o abierta (sin corcho). */
    private fun DrawScope.botella(c0: Offset, largo: Float, giro: Float, tipo: Int, oscuro: Boolean) {
        val u = density
        val g = largo * 0.36f
        rotate(giro, c0) {
            val cuerpo = Rect(c0.x - largo / 2, c0.y - g / 2, c0.x + largo * 0.22f, c0.y + g / 2)
            val cuello = Rect(cuerpo.right - 2 * u, c0.y - g * 0.2f, c0.x + largo * 0.45f, c0.y + g * 0.2f)
            val vidrio = t(VIDRIO_BOTELLA, oscuro, false)
            drawRoundRect(vidrio.copy(alpha = 0.55f), cuerpo.topLeft, cuerpo.size, CornerRadius(g * 0.35f))
            drawPath(Path().apply { moveTo(cuerpo.right - 2 * u, cuerpo.top + g * 0.1f); quadraticTo(cuerpo.right + g * 0.3f, c0.y - g * 0.2f, cuello.right, cuello.top); lineTo(cuello.right, cuello.bottom); quadraticTo(cuerpo.right + g * 0.3f, c0.y + g * 0.2f, cuerpo.right - 2 * u, cuerpo.bottom - g * 0.1f); close() }, vidrio.copy(alpha = 0.55f))
            // El mensaje enrollado dentro.
            val papel = if (tipo == 0) mezcla(CREMA_LONA, BIOLUZ, 0.35f) else t(CREMA_LONA, oscuro, false)
            val sale = if (tipo == 2) largo * 0.3f else 0f
            drawRoundRect(papel, Offset(cuerpo.left + g * 0.25f + sale, c0.y - g * 0.22f), Size(largo * 0.55f, g * 0.44f), CornerRadius(g * 0.2f))
            drawLine(t(TINTA, oscuro, false).copy(alpha = 0.3f), Offset(cuerpo.left + g * 0.4f + sale, c0.y), Offset(cuerpo.left + largo * 0.5f + sale, c0.y), 0.6f * u)
            if (tipo == 1) drawLine(t(ROJO, oscuro, false), Offset(cuerpo.left + largo * 0.32f, c0.y - g * 0.22f), Offset(cuerpo.left + largo * 0.32f, c0.y + g * 0.22f), 1 * u)
            // El brillo del vidrio.
            drawLine(Color.White.copy(alpha = 0.45f), Offset(cuerpo.left + g * 0.3f, cuerpo.top + g * 0.18f), Offset(cuerpo.right - g * 0.2f, cuerpo.top + g * 0.18f), 1.2f * u, StrokeCap.Round)
            drawRoundRect(vidrio, cuerpo.topLeft, cuerpo.size, CornerRadius(g * 0.35f), style = Stroke(0.8f * u))
            if (tipo != 2) {
                drawRect(t(CORCHO, oscuro, false), Offset(cuello.right - 1 * u, c0.y - g * 0.17f), Size(g * 0.35f, g * 0.34f))
                if (tipo == 1) {
                    drawRect(t(LATON, oscuro, false), Offset(cuello.right + g * 0.12f, c0.y - g * 0.21f), Size(g * 0.26f, g * 0.42f))
                    drawLine(t(CABO, oscuro, false), Offset(cuello.right, c0.y), Offset(cuello.right - g * 0.4f, c0.y + g * 0.5f), 0.8f * u)
                }
            }
        }
    }

    /**
     * La red de pesca tendida en el fondo: un cabo con boyas de corcho cruza arriba y de él cuelga la red en rombos, con las
     * cartas atrapadas como botellas con su mensaje (hasta diez): las que ya se pueden abrir brillan con la luz de la
     * bioluminiscencia (el primer degradado), las selladas llevan el corcho lacrado y su hilo, las abiertas, el mensaje
     * medio afuera. Las tres boyas grandes del cabo llevan pintada la cuenta de cada clase. Abajo, la arena con sus algas y
     * un ancla vieja; arriba, los haces de luz que bajan de la superficie. En modo oscuro, el abismo: no llega la luz,
     * la nieve marina cae y una medusa alumbra la red (el segundo degradado).
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(if (oscuro) mezcla(AGUA_HONDA, Color.Black, 0.35f) else mezcla(AGUA, Color(0xFF2E6A8E), 0.55f))
        if (!oscuro) for (j in 0 until 4) {
            val x0 = w * (0.1f + 0.25f * j)
            drawPath(Path().apply { moveTo(x0, 0f); lineTo(x0 + 26 * u, 0f); lineTo(x0 + 70 * u, h); lineTo(x0 + 30 * u, h); close() }, Color.White.copy(alpha = 0.06f))
        } else {
            mancha(Offset(w * 0.85f, h * 0.3f), w * 0.35f, BIOLUZ.copy(alpha = 0.18f))
            medusa(Offset(w * 0.86f, h * 0.28f), 26 * u, BIOLUZ, true)
            for (j in 0 until 30) drawCircle(Color.White.copy(alpha = 0.25f + 0.3f * azar(141, j)), (0.5f + 0.6f * azar(142, j)) * u, Offset(w * azar(143, j), h * azar(144, j)))
        }
        // La arena, las algas y el ancla.
        val arena = h * 0.86f
        drawPath(Path().apply { moveTo(0f, arena); quadraticTo(w * 0.4f, arena - 8 * u, w * 0.7f, arena - 2 * u); quadraticTo(w * 0.9f, arena + 2 * u, w, arena - 6 * u); lineTo(w, h); lineTo(0f, h); close() }, c(ARENA))
        for (j in 0 until 20) drawCircle(c(mezcla(ARENA, Color.Black, 0.25f)), (0.6f + azar(145, j)) * u, Offset(w * azar(146, j), arena + (h - arena) * azar(147, j)))
        listOf(0.04f, 0.1f, 0.94f).forEachIndexed { i, f ->
            val bx = w * f
            val p = Path().apply { moveTo(bx, arena); cubicTo(bx - 8 * u, arena - 20 * u, bx + 8 * u, arena - 36 * u, bx - 2 * u, arena - (52 - i * 10) * u) }
            drawPath(p, c(Color(0xFF3E7A4E)), style = Stroke(3 * u, cap = StrokeCap.Round))
        }
        val an = Offset(w * 0.06f, arena - 6 * u)
        drawLine(c(PLOMO), an, an + Offset(0f, -22 * u), 2.2f * u)
        drawCircle(c(PLOMO), 3 * u, an + Offset(0f, -25 * u), style = Stroke(1.4f * u))
        drawArc(c(PLOMO), 20f, 140f, false, Offset(an.x - 10 * u, an.y - 14 * u), Size(20 * u, 16 * u), style = Stroke(2.2f * u))
        // El cabo de arriba con sus boyas de corcho.
        val cy = 14 * u
        drawPath(Path().apply { moveTo(0f, cy - 4 * u); quadraticTo(w / 2, cy + 8 * u, w, cy - 4 * u) }, c(CABO), style = Stroke(2 * u))
        // La red: rombos que cuelgan del cabo.
        val red = Rect(w * 0.14f, cy, w * 0.86f, arena - 4 * u)
        val malla = 12 * u
        clipPath(Path().apply { moveTo(red.left, red.top); lineTo(red.right, red.top); lineTo(red.right - 14 * u, red.bottom); quadraticTo(red.center.x, red.bottom + 10 * u, red.left + 14 * u, red.bottom); close() }) {
            var x = red.left - red.height
            while (x < red.right) {
                drawLine(c(CABO).copy(alpha = 0.6f), Offset(x, red.top), Offset(x + red.height, red.bottom), 0.7f * u)
                drawLine(c(CABO).copy(alpha = 0.6f), Offset(x + red.height, red.top), Offset(x, red.bottom), 0.7f * u)
                x += malla
            }
        }
        // Las botellas atrapadas.
        val cartas = cartasBuzonCo(e, 10)
        val largo = minOf(red.width / 5.2f, 64 * u)
        cartas.forEachIndexed { i, tipo ->
            val fila = i / 5; val col = i % 5
            val p = Offset(red.left + red.width * (0.1f + 0.2f * col) + (azar(148, i) - 0.5f) * 8 * u, red.top + red.height * (0.42f + 0.32f * fila) + (azar(149, i) - 0.5f) * 8 * u)
            if (tipo == 0) mancha(p, largo * 0.65f, BIOLUZ.copy(alpha = 0.5f))
            botella(p, largo, (azar(150, i) - 0.5f) * 50f, tipo, oscuro)
        }
        // Las boyas grandes con la cuenta.
        val cuentas = listOf("POR ABRIR" to e.listas, "SELLADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        cuentas.forEachIndexed { i, (txt, n) ->
            val bx = w * (0.27f + 0.23f * i)
            val by = cy + 4 * u + (1f - ((bx - w / 2) / (w / 2)).let { it * it }) * 6 * u
            val br = Rect(bx - 30 * u, by + 2 * u, bx + 30 * u, by + 34 * u)
            drawLine(c(CABO), Offset(bx, by - 2 * u), Offset(bx, br.top), 1.4f * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), br.topLeft + Offset(1 * u, 1.5f * u), br.size, CornerRadius(br.height / 2))
            drawRoundRect(c(if (i == 0) BOYA else CREMA_LONA), br.topLeft, br.size, CornerRadius(br.height / 2))
            drawRect(c(GOMA).copy(alpha = 0.85f), Offset(br.left + 4 * u, br.top + br.height * 0.18f), Size(br.width - 8 * u, br.height * 0.64f))
            drawLine(Color.White.copy(alpha = 0.3f), Offset(br.left + 8 * u, br.top + 2.5f * u), Offset(br.right - 8 * u, br.top + 2.5f * u), 1.2f * u, StrokeCap.Round)
            textoEn(medidor, txt, Rect(br.left + 6 * u, br.top + br.height * 0.18f, br.right - 6 * u, br.top + br.height * 0.45f), letra, Color(0xFFF1F3F2), 7f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 4.5f)
            textoEn(medidor, "$n", Rect(br.left + 6 * u, br.top + br.height * 0.42f, br.right - 6 * u, br.top + br.height * 0.82f), letra, if (i == 0 && n > 0) BIOLUZ else Color(0xFFF1F3F2), 13f, peso = FontWeight.Bold, minimo = 6f)
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
 * La tapa es ese libro de tela de frente, con la cuña negra, la estrella en su círculo, la etiqueta «ОТЧЁТ · INFORME»
 * con la fecha, el sello violeta (de noche, la luna), la signatura y la ficha de préstamo asomando.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa: Rect = Rect(0.365f, 0.295f, 0.895f, 0.655f)
    override val letraTapa: FontFamily = LetraExtra.russo
    override val tamTapa: Float = 12.5f
    override val lineasTapa: Int = 3
    override val centradoTapa: Boolean = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La cartulina de la etiqueta de la tapa: hundida en oscuro y en la sala a oscuras de noche, pero clara para el extracto. */
    private fun cremaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = CREMA
        if (oscuro) r = mezcla(r, Color(0xFF0D0C0B), 0.28f)
        if (noche) r = mezcla(r, NOCHE, 0.13f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        fun c(x: Color) = t(x, oscuro, e.noche)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val crema = cremaTapa(oscuro, e.noche)
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val cu = Rect(w * 0.02f, h * 0.11f, w * 0.965f, h * 0.95f)
        val bisagra = cu.left + w * 0.06f
        // De noche, la lámpara de pantalla verde alumbra desde arriba a la izquierda (el único degradado).
        if (e.noche) mancha(Offset(w * 0.05f, 0f), w * 0.55f, LUZ.copy(alpha = 0.24f))
        drawRect(Color.Black.copy(alpha = 0.22f), cu.topLeft + Offset(3 * u, 5 * u), cu.size)
        // La ficha de préstamo que asoma de su bolsillo por arriba, con los sellos de fecha: más cuanto más escribiste.
        val fc = Rect(w * 0.6f, h * 0.015f, w * 0.84f, h * 0.2f)
        drawRect(Color.Black.copy(alpha = 0.15f), fc.topLeft + Offset(1 * u, 1 * u), fc.size)
        drawRect(c(CREMA), fc.topLeft, fc.size)
        drawLine(c(ROJO).copy(alpha = 0.7f), Offset(fc.left, fc.top + 3 * u), Offset(fc.right, fc.top + 3 * u), 0.7f * u)
        textoEn(medidor, "ФОРМУЛЯР", Rect(fc.left + 3 * u, fc.top + 4 * u, fc.right - 3 * u, fc.top + 10 * u), letra, c(TINTA).copy(alpha = 0.7f), 6.5f, espaciado = 1f, minimo = 4.5f)
        val sellos = 1 + (lleno * 3).toInt()
        for (j in 0 until sellos) {
            val o = Offset(fc.left + fc.width * (0.08f + 0.23f * j), fc.top + 11.5f * u)
            rotate(-6f + 12f * azar(e.semilla, 60 + j), Offset(o.x + fc.width * 0.1f, o.y + 2 * u)) {
                drawRoundRect(c(VIOLETA).copy(alpha = 0.65f), o, Size(fc.width * 0.2f, 4 * u), CornerRadius(1 * u), style = Stroke(0.6f * u))
                drawLine(c(VIOLETA).copy(alpha = 0.6f), Offset(o.x + 2 * u, o.y + 2 * u), Offset(o.x + fc.width * 0.2f - 2 * u, o.y + 2 * u), 0.8f * u)
            }
        }
        // El canto de las hojas.
        drawRect(c(CREMA), Offset(cu.left + 3 * u, cu.top + 3 * u), cu.size)
        for (k in 1..3) drawLine(c(NOGAL).copy(alpha = 0.3f), Offset(cu.left + 8 * u, cu.bottom + k * 0.7f * u), Offset(cu.right + 2 * u, cu.bottom + k * 0.7f * u), 0.4f * u)
        // Por dentro (se ve al abrirse): el formulario de lectura, con su cuadrícula de casillas y un sello violeta.
        val hoja = Rect(bisagra, cu.top, cu.right, cu.bottom)
        if (a > 0.01f) {
            drawRect(c(CREMA), hoja.topLeft, hoja.size)
            val x0 = hoja.left + 10 * u; val x1 = hoja.right - 10 * u
            drawRect(c(TINTA).copy(alpha = 0.7f), Offset(x0, hoja.top + 10 * u), Size(x1 - x0, 8 * u))
            var ly = hoja.top + 26 * u
            while (ly < hoja.bottom - 8 * u) { drawLine(c(TINTA).copy(alpha = 0.3f), Offset(x0, ly), Offset(x1, ly), 0.5f * u); ly += 9 * u }
            listOf(0.12f, 0.4f, 0.72f).forEach { f -> drawLine(c(TINTA).copy(alpha = 0.3f), Offset(x0 + (x1 - x0) * f, hoja.top + 18 * u), Offset(x0 + (x1 - x0) * f, hoja.bottom - 8 * u), 0.5f * u) }
            val sv = Offset(x1 - (x1 - x0) * 0.16f, hoja.bottom - hoja.height * 0.3f)
            drawCircle(c(VIOLETA).copy(alpha = 0.6f), h * 0.08f, sv, style = Stroke(0.9f * u))
            drawCircle(c(VIOLETA).copy(alpha = 0.5f), h * 0.05f, sv, style = Stroke(0.6f * u))
        }
        // La tapa de tela, que gira sobre el lomo al abrirse.
        val sx = 1f - a
        if (sx > 0.02f) scale(sx, 1f, Offset(bisagra, cu.center.y)) {
            drawRect(tela, cu.topLeft, cu.size)
            var k = cu.top
            while (k < cu.bottom) { drawLine(Color.White.copy(alpha = 0.04f), Offset(cu.left, k), Offset(cu.right, k), 0.5f * u); k += 2 * u }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cu.right - 4 * u, cu.top), Size(4 * u, cu.height))
            // La cuña negra que cruza la tapa en diagonal.
            clipRect(cu.left, cu.top, cu.right, cu.bottom) {
                drawPath(Path().apply {
                    moveTo(bisagra, cu.bottom); lineTo(w * 0.47f, cu.top); lineTo(w * 0.57f, cu.top); lineTo(bisagra + w * 0.11f, cu.bottom); close()
                }, c(TINTA).copy(alpha = 0.88f))
                drawLine(c(ROJO).copy(alpha = 0.8f), Offset(bisagra + w * 0.13f, cu.bottom), Offset(w * 0.59f, cu.top), 1.6f * u)
            }
            // La estrella roja en su círculo blanco.
            val ce = Offset(w * 0.185f, h * 0.36f)
            val rc = minOf(h * 0.13f, w * 0.1f)
            drawCircle(Color.Black.copy(alpha = 0.25f), rc, Offset(ce.x + 1 * u, ce.y + 1.5f * u))
            drawCircle(crema, rc, ce)
            drawCircle(c(TINTA), rc, ce, style = Stroke(1.6f * u))
            drawCircle(c(TINTA).copy(alpha = 0.5f), rc * 0.86f, ce, style = Stroke(0.6f * u))
            drawPath(estrella(ce, rc * 0.74f, 5, 0.42f), c(ROJO))
            drawPath(estrella(ce, rc * 0.74f, 5, 0.42f), c(ROJO_OSC), style = Stroke(0.7f * u))
            // El sello violeta de la biblioteca, con su estrella; de noche, una luna.
            val cs = Offset(w * 0.19f, h * 0.665f)
            val rs = minOf(w * 0.075f, h * 0.085f)
            if (e.noche) {
                drawCircle(LUNA, rs * 0.75f, cs)
                drawCircle(tela, rs * 0.64f, Offset(cs.x + rs * 0.34f, cs.y - rs * 0.2f))
                drawCircle(LUNA.copy(alpha = 0.7f), 0.9f * u, Offset(cs.x - rs * 0.9f, cs.y - rs * 0.8f))
            } else {
                rotate(-12f, cs) {
                    drawCircle(c(VIOLETA).copy(alpha = 0.8f), rs, cs, style = Stroke(1 * u))
                    drawCircle(c(VIOLETA).copy(alpha = 0.65f), rs * 0.66f, cs, style = Stroke(0.6f * u))
                    drawPath(estrella(cs, rs * 0.42f, 5, 0.42f), c(VIOLETA).copy(alpha = 0.7f))
                    for (j in 0 until 12) {
                        val g = Math.toRadians(j * 30.0).toFloat()
                        drawCircle(c(VIOLETA).copy(alpha = 0.6f), 0.55f * u, Offset(cs.x + rs * 0.83f * cos(g), cs.y + rs * 0.83f * sin(g)))
                    }
                }
            }
            // El tejuelo con la signatura del diario.
            val tj = Rect(w * 0.075f, h * 0.8f, w * 0.29f, h * 0.9f)
            drawRect(crema, tj.topLeft, tj.size)
            drawRect(c(TINTA).copy(alpha = 0.6f), tj.topLeft, tj.size, style = Stroke(0.7f * u))
            drawLine(c(TINTA).copy(alpha = 0.5f), Offset(tj.left + tj.width * 0.15f, tj.top + tj.height * 0.24f), Offset(tj.right - tj.width * 0.15f, tj.top + tj.height * 0.24f), 0.6f * u)
            textoEn(medidor, "Д-${(e.semilla and 0x7fffffff) % 900 + 100}", Rect(tj.left + 2 * u, tj.top + tj.height * 0.3f, tj.right - 2 * u, tj.bottom - 1 * u), letra, c(TINTA), 9f, minimo = 5.5f)
            // La etiqueta con su banda negra: el informe, el extracto, la fecha y las palabras en sus renglones.
            val et = Rect(w * 0.34f, h * 0.175f, w * 0.92f, h * 0.855f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.2f * u, 1.8f * u), et.size)
            drawRect(crema, et.topLeft, et.size)
            drawRect(c(TINTA), et.topLeft, et.size, style = Stroke(1.8f * u))
            val banda = Rect(et.left, et.top, et.right, h * 0.27f)
            drawRect(c(TINTA), banda.topLeft, banda.size)
            textoEn(medidor, "ОТЧЁТ · INFORME", banda.deflate(3 * u), letra, crema, 10f, espaciado = 1.5f, minimo = 6f)
            textoEn(medidor, e.fecha, Rect(et.left + 8 * u, h * 0.672f, et.right - 8 * u, h * 0.745f), letra, c(TINTA), 10f, minimo = 6f)
            drawLine(c(TINTA).copy(alpha = 0.3f), Offset(et.left + 8 * u, h * 0.752f), Offset(et.right - 8 * u, h * 0.752f), 0.6f * u)
            if (e.palabras > 0 && !e.vacio) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(et.left + 8 * u, h * 0.762f, et.right - 8 * u, h * 0.828f), letra, c(TINTA).copy(alpha = 0.7f), 9f, minimo = 6f)
            }
            drawLine(c(TINTA).copy(alpha = 0.3f), Offset(et.left + 8 * u, h * 0.835f), Offset(et.right - 8 * u, h * 0.835f), 0.6f * u)
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.35f * a), cu.topLeft, cu.size)
        }
        if (a > 0.01f && sx > 0.02f) {
            val borde = bisagra + (cu.right - bisagra) * sx
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(borde, cu.top), Size(14 * u * kotlin.math.sin(Math.PI.toFloat() * a), cu.height))
        }
        // El lomo: la cinta de tela negra con sus dos grapas.
        drawRect(c(TINTA), cu.topLeft, Size(bisagra - cu.left, cu.height))
        var lx = cu.left + 1.5f * u
        while (lx < bisagra) { drawLine(Color.White.copy(alpha = 0.05f), Offset(lx, cu.top), Offset(lx, cu.bottom), 0.5f * u); lx += 1.8f * u }
        listOf(0.3f, 0.7f).forEach { f ->
            val gx = (cu.left + bisagra) / 2
            drawRect(c(Color(0xFFB0B0B0)), Offset(gx - 1.2f * u, cu.top + cu.height * f - h * 0.05f), Size(2.4f * u, h * 0.1f))
            drawRect(Color.White.copy(alpha = 0.4f), Offset(gx - 1.2f * u, cu.top + cu.height * f - h * 0.05f), Size(0.8f * u, h * 0.1f))
        }
        drawLine(Color.White.copy(alpha = 0.12f), Offset(bisagra, cu.top), Offset(bisagra, cu.bottom), 0.8f * u)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /**
     * La cabecera del informe, a la manera constructivista: a la izquierda, el círculo rojo con la estrella crema, cruzado por
     * una barra negra en diagonal; arriba, la banda negra con «ОТЧЁТ · INFORME DE JORNADA» y una cuña roja que la corta; la
     * fecha y el detalle en letra de molde; al pie, la barra roja con sus rayas en diagonal y un filete negro. De noche, la
     * luna creciente en lugar de la estrella (que queda chica a su lado) y la banda en rojo apagado.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val negro = if (e.noche) p.tenue else c(TINTA)
        val ce = Offset(22 * u, 42 * u)
        rotate(-32f, ce) { drawRect(negro, Offset(ce.x - 30 * u, ce.y - 3 * u), Size(62 * u, 6 * u)) }
        drawCircle(c(ROJO), 19 * u, ce)
        if (e.noche) {
            crecienteCon(ce + Offset(-2 * u, 0f), 11 * u, c(CREMA))
            drawPath(estrella(ce + Offset(9 * u, -8 * u), 4.5f * u, 5, 0.42f), c(CREMA))
        } else {
            drawPath(estrella(ce, 12 * u, 5, 0.42f), c(CREMA))
        }
        // La banda negra con el rótulo, cortada por la cuña roja.
        val banda = Rect(48 * u, 6 * u, w, 26 * u)
        drawRect(if (e.noche) mezcla(c(ROJO), Color.Black, 0.35f) else c(TINTA), banda.topLeft, banda.size)
        drawPath(Path().apply { moveTo(w - 30 * u, banda.top); lineTo(w - 18 * u, banda.top); lineTo(w - 30 * u, banda.bottom); lineTo(w - 42 * u, banda.bottom); close() }, c(ROJO))
        drawPath(Path().apply { moveTo(w - 12 * u, banda.top); lineTo(w - 6 * u, banda.top); lineTo(w - 18 * u, banda.bottom); lineTo(w - 24 * u, banda.bottom); close() }, c(ROJO))
        textoEn(medidor, e.rotulo, Rect(banda.left + 8 * u, banda.top, banda.right - 46 * u, banda.bottom), LetraExtra.russo, if (e.noche) p.tinta else c(CREMA), 11f, espaciado = 0.6f, minimo = 6.5f)
        fechaCon(medidor, e.fecha, Rect(50 * u, 31 * u, w - 2 * u, 75 * u), LetraExtra.russo, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(50 * u, 77 * u, w - 2 * u, 93 * u), LetraExtra.russo, p.tenue, 10.5f, minimo = 6.5f)
        // La barra roja del pie, con sus rayas en diagonal, y el filete negro.
        drawRect(c(ROJO), Offset(0f, 104 * u), Size(w * 0.7f, 4 * u))
        var x = w * 0.74f
        while (x < w - 4 * u) {
            drawPath(Path().apply { moveTo(x, 108 * u); lineTo(x + 4 * u, 104 * u); lineTo(x + 7 * u, 104 * u); lineTo(x + 3 * u, 108 * u); close() }, c(ROJO))
            x += 8 * u
        }
        drawRect(negro, Offset(0f, 111 * u), Size(w, 1.3f * u))
    }

    /**
     * La libreta escolar del informe: el margen rojo doble a la derecha, la cuña roja y negra abajo a la izquierda con su
     * segmento de círculo, como en los carteles; el sello violeta de la biblioteca abajo a la derecha, torcido, y tres barras
     * en diagonal arriba a la izquierda. En modo oscuro, la luz de la lámpara de escritorio de pantalla verde, que está arriba a la
     * derecha, con su cono.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, false, noche)
        drawLine(p.acento.copy(alpha = 0.5f), Offset(w - 22 * u, 0f), Offset(w - 22 * u, h), 1.5f * u)
        drawLine(p.acento.copy(alpha = 0.3f), Offset(w - 18.5f * u, 0f), Offset(w - 18.5f * u, h), 0.7f * u)
        // La cuña de los carteles, abajo a la izquierda.
        drawArc(p.acento.copy(alpha = 0.18f), 270f, 90f, true, Offset(-70 * u, h - 70 * u), Size(140 * u, 140 * u))
        drawPath(Path().apply { moveTo(0f, h); lineTo(52 * u, h); lineTo(0f, h - 52 * u); close() }, p.acento.copy(alpha = 0.6f))
        drawPath(Path().apply { moveTo(0f, h - 52 * u); lineTo(52 * u, h); lineTo(64 * u, h); lineTo(0f, h - 64 * u); close() }, p.tinta.copy(alpha = 0.7f))
        // Tres barras en diagonal arriba a la izquierda.
        rotate(-35f, Offset(30 * u, 70 * u)) {
            listOf(0f to p.acento.copy(alpha = 0.14f), 9f to p.tinta.copy(alpha = 0.1f), 15f to p.acento.copy(alpha = 0.1f)).forEach { (dy, col) ->
                drawRect(col, Offset(-10 * u, 70 * u + dy * u), Size(80 * u, if (dy == 0f) 5 * u else 3 * u))
            }
        }
        // El sello violeta de la biblioteca.
        val se = Offset(w - 60 * u, h * 0.74f)
        rotate(-14f, se) {
            drawCircle(c(VIOLETA).copy(alpha = 0.22f), 22 * u, se, style = Stroke(1.4f * u))
            drawCircle(c(VIOLETA).copy(alpha = 0.18f), 17 * u, se, style = Stroke(0.7f * u))
            drawPath(estrella(se, 9 * u, 5, 0.42f), c(VIOLETA).copy(alpha = 0.18f))
            drawLine(c(VIOLETA).copy(alpha = 0.2f), Offset(se.x - 28 * u, se.y + 26 * u), Offset(se.x + 28 * u, se.y + 26 * u), 1 * u)
        }
        if (oscuro && !p.oscura) {
            val lampara = Offset(w * 0.95f, h * 0.03f)
            penumbraCon(lampara, LUZ, 0.18f)
            hazCon(lampara, 118f, 34f, h * 0.75f, LUZ.copy(alpha = 0.035f))
        }
    }

    /** Al pie de la barra, la franja roja y el filete negro, con una cuña negra a la izquierda. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        fondoBarraCon(p, oscuro)
        drawRect(c(ROJO), Offset(0f, h - 7.5f * u), Size(w, 3.5f * u))
        drawRect(c(TINTA), Offset(0f, h - 2.5f * u), Size(w, 1.4f * u))
        drawPath(Path().apply { moveTo(0f, h - 7.5f * u); lineTo(46 * u, h - 7.5f * u); lineTo(40 * u, h - 4 * u); lineTo(0f, h - 4 * u); close() }, c(TINTA))
    }

    /** Un sello de goma rojo: el bloque entintado con su recuadro crema y una estrella en la punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = tonoBotonCon(x, oscuro, noche)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(1.5f * u, 2 * u), size)
        drawRect(c(ROJO), Offset.Zero, size)
        drawRect(c(CREMA), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), style = Stroke(1 * u))
        drawPath(estrella(Offset(9.5f * u, h / 2), 3.6f * u, 5, 0.42f), c(CREMA))
        // La tinta irregular del sello en las orillas.
        for (k in 0 until 10) drawCircle(c(ROJO_OSC).copy(alpha = 0.5f), (0.6f + azar(17, k)) * u, Offset(w * azar(17, k + 20), if (k % 2 == 0) 1.2f * u else h - 1.2f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = CREMA

    // ---------------------------------------------------------------- la libreta de momentos

    /** Las tapas de papel de los cuadernos escolares, la pizarra, el verde de la pared del aula y la tinta violeta. */
    private val TAPAS_TETRAD = listOf(Color(0xFF4E7A5A), Color(0xFF5A78A0), Color(0xFFC98A8A), Color(0xFFCDBB8E), Color(0xFF8A7AB0))
    private val PIZARRON = Color(0xFF26402F)
    private val TIZA = Color(0xFFEDEDE4)
    private val PARED_AULA = Color(0xFF7FA08A)
    private val PAPEL_ESCOLAR = Color(0xFFF7F3E6)
    private val CUADRICULA = Color(0xFF8AA6C8)
    private val GRAPA = Color(0xFFC3C6CA)

    override val altoLibreta: Dp = 256.dp

    /**
     * El cuaderno escolar sobre el pupitre del aula: arriba, el borde de la pizarra con su repisa de tizas y el borrador,
     * en la pared pintada de verde hasta media altura y encalada arriba, con la lámpara de globo colgando. El pupitre de
     * madera barnizada tiene su ranura para las plumas y, en su agujero, el tintero de cristal que no se derrama; al lado, el
     * portaplumas con su plumilla, la regla de madera y la goma. El cuaderno (тетрадь) es de tapa de papel verde con el
     * patrón impreso, la etiqueta enmarcada con «ТЕТРАДЬ» y, en tinta violeta, el nombre a mano y la cuenta, y el lomo con
     * sus dos grapas; como engorda poco, con las hojas se le apilan debajo los cuadernos anteriores (uno cada 40 hojas, hasta
     * 4, de tapas de otros colores). Al abrirse, la tapa gira sobre las grapas: dentro, la hoja de cuadritos con su margen rojo,
     * la letra violeta y el «5» de la maestra en rojo. En modo oscuro, el aula al anochecer: solo alumbra la lámpara de globo
     * (el único degradado).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val lleno = llenoLibreta(e.hojas)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF0A0A09), 0.45f) else x
        fun cp(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0A09), 0.28f) else x
        // La pared: encalada arriba, verde abajo, con su filete; la pizarra con su marco y la repisa.
        val pupitre = h * 0.42f
        drawRect(c(PARED), Offset.Zero, Size(w, pupitre))
        drawRect(c(PARED_AULA), Offset(0f, h * 0.22f), Size(w, pupitre - h * 0.22f))
        drawRect(c(ROJO_OSC), Offset(0f, h * 0.22f), Size(w, 2 * u))
        val pz = Rect(w * 0.3f, -8 * u, w * 1.05f, h * 0.17f)
        drawRect(c(NOGAL), pz.inflate(4 * u).topLeft, pz.inflate(4 * u).size)
        drawRect(c(PIZARRON), pz.topLeft, pz.size)
        drawPath(Path().apply { moveTo(pz.left + 14 * u, pz.bottom - 14 * u); quadraticTo(pz.left + 60 * u, pz.bottom - 30 * u, pz.left + 110 * u, pz.bottom - 18 * u) }, c(TIZA).copy(alpha = 0.18f), style = Stroke(5 * u))
        textoEn(medidor, "2 × 7 = 14", Rect(pz.left + 120 * u, pz.top + 12 * u, pz.left + 200 * u, pz.bottom - 6 * u), letra, c(TIZA).copy(alpha = 0.7f), 12f)
        drawRect(c(NOGAL_LUZ), Offset(pz.left - 6 * u, pz.bottom + 4 * u), Size(pz.width + 12 * u, 5 * u))
        drawRect(c(TIZA), Offset(pz.left + 20 * u, pz.bottom + 1.5f * u), Size(12 * u, 3 * u))
        drawRect(c(BAQUELITA), Offset(pz.left + 44 * u, pz.bottom - 2 * u), Size(22 * u, 6 * u)); drawRect(c(TELA_RADIO), Offset(pz.left + 44 * u, pz.bottom + 2 * u), Size(22 * u, 2 * u))
        // La lámpara de globo que cuelga del techo.
        val gl = Offset(w * 0.14f, h * 0.11f)
        drawLine(c(BAQUELITA), Offset(gl.x, 0f), Offset(gl.x, gl.y - 14 * u), 1.2f * u)
        drawRect(c(LATON), Offset(gl.x - 4 * u, gl.y - 16 * u), Size(8 * u, 4 * u))
        drawCircle(if (oscuro) LUZ else c(CREMA), 14 * u, gl)
        drawCircle(Color.White.copy(alpha = 0.3f), 5 * u, gl + Offset(-5 * u, -5 * u))
        if (oscuro) drawRect(Brush.radialGradient(listOf(LUZ.copy(alpha = 0.32f), LUZ.copy(alpha = 0.1f), Color.Transparent), gl, w * 0.9f))
        // El pupitre: el tablero de madera barnizada, la ranura de las plumas y el canto.
        drawRect(c(NOGAL_LUZ), Offset(0f, pupitre), Size(w, h - pupitre))
        vetas(Rect(0f, pupitre, w, h), c(NOGAL_VETA).copy(alpha = 0.55f), 210, densidad = 0.9f)
        drawRect(Color.White.copy(alpha = 0.12f), Offset(0f, pupitre), Size(w, 2 * u))
        drawRect(c(NOGAL), Offset(0f, pupitre + 6 * u), Size(w, 9 * u))
        drawRect(c(NOGAL_VETA), Offset(w * 0.05f, pupitre + 8.5f * u), Size(w * 0.6f, 4 * u))
        drawRect(c(NOGAL), Offset(0f, h - 8 * u), Size(w, 8 * u))
        // El portaplumas en su ranura, con su plumilla.
        drawRect(c(ROJO_OSC), Offset(w * 0.12f, pupitre + 8 * u), Size(w * 0.3f, 3.6f * u))
        drawRect(c(LATON), Offset(w * 0.42f, pupitre + 8 * u), Size(6 * u, 3.6f * u))
        drawPath(Path().apply { moveTo(w * 0.42f + 6 * u, pupitre + 8 * u); lineTo(w * 0.42f + 14 * u, pupitre + 9.8f * u); lineTo(w * 0.42f + 6 * u, pupitre + 11.6f * u); close() }, c(ALUMINIO_URSS))
        // El tintero de cristal en su agujero, a la derecha.
        val ti = Offset(w * 0.88f, pupitre + 30 * u)
        drawOval(c(NOGAL_VETA), Offset(ti.x - 17 * u, ti.y - 7 * u), Size(34 * u, 14 * u))
        drawPath(Path().apply { moveTo(ti.x - 13 * u, ti.y); lineTo(ti.x - 9 * u, ti.y - 16 * u); lineTo(ti.x + 9 * u, ti.y - 16 * u); lineTo(ti.x + 13 * u, ti.y); close() }, c(Color(0xFFBFD6D8)).copy(alpha = 0.85f))
        drawPath(Path().apply { moveTo(ti.x - 10 * u, ti.y - 2 * u); lineTo(ti.x - 7 * u, ti.y - 10 * u); lineTo(ti.x + 7 * u, ti.y - 10 * u); lineTo(ti.x + 10 * u, ti.y - 2 * u); close() }, c(VIOLETA))
        drawOval(c(Color(0xFFDDEBEC)), Offset(ti.x - 9 * u, ti.y - 19 * u), Size(18 * u, 6 * u))
        drawOval(c(BAQUELITA), Offset(ti.x - 4 * u, ti.y - 17.5f * u), Size(8 * u, 3 * u))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(ti.x - 7 * u, ti.y - 14 * u), Offset(ti.x - 10 * u, ti.y - 3 * u), 1.2f * u)
        // La regla de madera y la goma, abajo a la izquierda.
        rotate(-8f, Offset(w * 0.1f, h * 0.88f)) {
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(w * 0.01f + 2 * u, h * 0.86f + 3 * u), Size(w * 0.2f, 10 * u))
            drawRect(c(Color(0xFFD9B97E)), Offset(w * 0.01f, h * 0.86f), Size(w * 0.2f, 10 * u))
            var mx = w * 0.01f + 3 * u; var j = 0
            while (mx < w * 0.21f) { drawLine(c(TINTA).copy(alpha = 0.6f), Offset(mx, h * 0.86f), Offset(mx, h * 0.86f + (if (j % 5 == 0) 4.5f else 2.5f) * u), 0.6f * u); mx += 2.5f * u; j++ }
        }
        rotate(14f, Offset(w * 0.9f, h * 0.84f)) {
            drawRect(c(Color(0xFFC94A4A)), Offset(w * 0.9f - 10 * u, h * 0.84f - 5 * u), Size(11 * u, 10 * u))
            drawRect(c(Color(0xFF4A6AA8)), Offset(w * 0.9f + 1 * u, h * 0.84f - 5 * u), Size(9 * u, 10 * u))
        }
        // El cuaderno, con los anteriores apilados debajo.
        val ancho = w * 0.52f; val alto = minOf(ancho * 0.72f, h * 0.5f)
        val r = Rect(w * 0.24f, h * 0.42f, w * 0.24f + ancho, h * 0.42f + alto)
        val g = (1.5f + 2.5f * lleno) * u
        val tapa = TAPAS_TETRAD[0]
        val previos = (e.hojas / 40).coerceAtMost(4)
        rotate(-3f, r.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), r.topLeft + Offset(5 * u, 5 * u + previos * 3.5f * u + g), r.size)
            for (j in previos downTo 1) {
                val o = Offset((j * 3f - 4f * azar(e.semilla, 220 + j)) * u, j * 3.5f * u)
                rotate(-3f + 6f * azar(e.semilla, 230 + j), r.center + o) {
                    drawRect(c(PAPEL_ESCOLAR), r.topLeft + o + Offset(0f, 1.5f * u), r.size)
                    drawRect(c(TAPAS_TETRAD[j % TAPAS_TETRAD.size]), r.topLeft + o, r.size)
                }
            }
            drawRect(c(mezcla(tapa, Color.Black, 0.2f)), r.topLeft + Offset(0f, g), r.size)
            cantoLibreta(r, g, c(PAPEL_ESCOLAR), c(CUADRICULA).copy(alpha = 0.5f))
            // Por dentro: la hoja de cuadritos con el margen rojo, la letra violeta y el «5» en rojo.
            if (a > 0.01f) {
                drawRect(cp(PAPEL_ESCOLAR), r.topLeft, r.size)
                var gx = r.left + 5 * u
                while (gx < r.right) { drawLine(c(CUADRICULA).copy(alpha = 0.45f), Offset(gx, r.top), Offset(gx, r.bottom), 0.5f * u); gx += 5 * u }
                var gy = r.top + 5 * u
                while (gy < r.bottom) { drawLine(c(CUADRICULA).copy(alpha = 0.45f), Offset(r.left, gy), Offset(r.right, gy), 0.5f * u); gy += 5 * u }
                drawLine(c(ROJO), Offset(r.right - 18 * u, r.top), Offset(r.right - 18 * u, r.bottom), 0.9f * u)
                for (j in 0 until 6) {
                    val y = r.top + 14 * u + j * 15 * u
                    if (y > r.bottom - 8 * u) break
                    val p = Path().apply { moveTo(r.left + 14 * u, y); var x = r.left + 14 * u; var k = 0
                        val fin = r.right - (if (j % 3 == 2) 70 else 26) * u
                        while (x < fin) { x += 3.5f * u; lineTo(x, y + (if (k % 2 == 0) -2.5f else 0.5f) * u); k++ } }
                    drawPath(p, c(VIOLETA).copy(alpha = 0.8f), style = Stroke(0.8f * u))
                }
                val nota = Offset(r.right - 9 * u, r.top + 18 * u)
                textoEn(medidor, "5", Rect(nota.x - 8 * u, nota.y - 9 * u, nota.x + 8 * u, nota.y + 9 * u), letra, c(ROJO), 14f, peso = FontWeight.Bold)
                drawCircle(c(ROJO), 9 * u, nota, style = Stroke(1 * u))
            }
            // La tapa de papel, que gira sobre las grapas.
            val sx = 1f - a
            if (sx > 0.02f) scale(sx, 1f, Offset(r.left, r.center.y)) {
                drawRect(c(tapa), r.topLeft, r.size)
                var y = r.top + 4 * u; var k = 0
                while (y < r.bottom) { var x = r.left + (if (k % 2 == 0) 4f else 10f) * u
                    while (x < r.right) { drawPath(Path().apply { moveTo(x, y - 2 * u); lineTo(x + 2 * u, y); lineTo(x, y + 2 * u); lineTo(x - 2 * u, y); close() }, Color.White.copy(alpha = 0.08f)); x += 12 * u }
                    y += 6 * u; k++ }
                // La etiqueta con marco doble: «ТЕТРАДЬ» impreso y, a mano, el nombre y la cuenta.
                val et = Rect(r.left + ancho * 0.16f, r.top + alto * 0.14f, r.right - ancho * 0.1f, r.top + alto * 0.68f)
                drawRect(cp(PAPEL_ESCOLAR), et.topLeft, et.size)
                drawRect(c(TINTA).copy(alpha = 0.75f), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(1 * u))
                drawRect(c(TINTA).copy(alpha = 0.5f), et.deflate(5 * u).topLeft, et.deflate(5 * u).size, style = Stroke(0.5f * u))
                textoEn(medidor, "ТЕТРАДЬ", Rect(et.left + 8 * u, et.top + 6 * u, et.right - 8 * u, et.top + et.height * 0.3f), letra, c(TINTA), 11f, peso = FontWeight.Bold, espaciado = 2.5f, minimo = 7f)
                listOf(0.58f, 0.84f).forEach { f -> drawLine(c(TINTA).copy(alpha = 0.4f), Offset(et.left + 10 * u, et.top + et.height * f), Offset(et.right - 10 * u, et.top + et.height * f), 0.6f * u) }
                textoEn(medidor, e.nombre, Rect(et.left + 10 * u, et.top + et.height * 0.32f, et.right - 10 * u, et.top + et.height * 0.58f), LetraExtra.trazo, c(VIOLETA), 16f, peso = FontWeight.Bold, minimo = 8f)
                textoEn(medidor, e.cuenta, Rect(et.left + 10 * u, et.top + et.height * 0.6f, et.right - 10 * u, et.top + et.height * 0.84f), letra, c(VIOLETA), 9.5f, minimo = 6f)
                drawPath(estrella(Offset(r.center.x + ancho * 0.03f, r.top + alto * 0.83f), alto * 0.08f, 5, 0.4f), c(ROJO))
                if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), r.topLeft, r.size)
            }
            if (a > 0.01f && sx > 0.02f) drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left + ancho * sx, r.top), Size(12 * u * sin(PI.toFloat() * a), r.height))
            // El lomo con sus dos grapas.
            drawRect(c(mezcla(tapa, Color.Black, 0.25f)), r.topLeft, Size(ancho * 0.04f, r.height))
            listOf(0.28f, 0.72f).forEach { f -> drawRect(c(GRAPA), Offset(r.left + ancho * 0.012f, r.top + alto * f - 7 * u), Size(2 * u, 14 * u)) }
        }
    }

    private val ALUMINIO_URSS = Color(0xFFB9BEC2)

    /**
     * La pestaña del día es un recorte de hoja de cuadritos con su margen rojo: la letra de la semana arriba, el día grande en
     * tinta violeta y abajo la nota de la maestra, un círculo rojo relleno del color del ánimo (sin ánimo, el círculo vacío).
     * La esquina de arriba se dobla un poco según la semilla.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
        val oreja = (3f + 4f * azar(e.semilla, 4)) * u
        val forma = Path().apply { moveTo(r.left, r.top); lineTo(r.right - oreja, r.top); lineTo(r.right, r.top + oreja); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close() }
        translate(1 * u, 1.4f * u) { drawPath(forma, Color.Black.copy(alpha = 0.22f)) }
        drawPath(forma, c(PAPEL_ESCOLAR))
        var gx = r.left + 4 * u
        while (gx < r.right) { drawLine(c(CUADRICULA).copy(alpha = 0.4f), Offset(gx, r.top), Offset(gx, r.bottom), 0.5f * u); gx += 4 * u }
        var gy = r.top + 4 * u
        while (gy < r.bottom) { drawLine(c(CUADRICULA).copy(alpha = 0.4f), Offset(r.left, gy), Offset(r.right, gy), 0.5f * u); gy += 4 * u }
        drawLine(c(ROJO), Offset(r.left + 5 * u, r.top), Offset(r.left + 5 * u, r.bottom), 0.9f * u)
        drawPath(Path().apply { moveTo(r.right - oreja, r.top); lineTo(r.right - oreja, r.top + oreja); lineTo(r.right, r.top + oreja); close() }, c(mezcla(PAPEL_ESCOLAR, Color.Black, 0.15f)))
        textoEn(medidor, e.semana, Rect(r.left + 6 * u, r.top + 2 * u, r.right - 6 * u, r.top + 11 * u), letra, c(TINTA).copy(alpha = 0.7f), 7.5f, minimo = 5f)
        textoEn(medidor, e.dia, Rect(r.left + 6 * u, r.top + 11 * u, r.right - 2 * u, r.top + 33 * u), letra, c(VIOLETA), 17f, peso = FontWeight.Bold, minimo = 9f)
        val n = Offset(r.center.x + 2.5f * u, r.bottom - 9 * u)
        if (e.tono != null) drawCircle(e.tono, 6 * u, n)
        drawCircle(c(ROJO), 6.5f * u, n, style = Stroke(1.1f * u))
    }

    /** El mes escrito con tiza en una franja de pizarra con su marco de madera y la repisa, como el encabezado de la clase. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(NOGAL))
        val pz = Rect(4 * u, 3 * u, w - 4 * u, h - 6 * u)
        drawRect(c(PIZARRON), pz.topLeft, pz.size)
        for (k in 0 until 4) drawPath(Path().apply { val x0 = pz.left + pz.width * azar(240, k); moveTo(x0, pz.bottom - 4 * u); quadraticTo(x0 + 20 * u, pz.top + 6 * u, x0 + 46 * u, pz.top + 10 * u) },
            c(TIZA).copy(alpha = 0.07f), style = Stroke(6 * u))
        drawRect(c(NOGAL_LUZ), Offset(0f, h - 4 * u), Size(w, 4 * u))
        drawRect(c(TIZA), Offset(w - 30 * u, h - 6 * u), Size(10 * u, 2.5f * u))
        textoEn(medidor, texto, Rect(pz.left + 14 * u, pz.top + 2 * u, pz.right - 14 * u, pz.bottom - 2 * u), LetraExtra.trazo, c(TIZA).copy(alpha = 0.92f), 17f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El papel de sobre gris crema, el sello de sello de lacre (el «surguch»), el bramante y la pintura de los buzones. */
    private val SOBRE = Color(0xFFE3D9C3)
    private val SOBRE_SOMBRA = Color(0xFFA89C82)
    private val SOBRE_LUZ = Color(0xFFF0E8D6)
    private val SURGUCH = Color(0xFF9C1A16)
    private val BRAMANTE = Color(0xFFBFA77A)
    private val AZUL_IMPRESO = Color(0xFF2E4A8A)
    private val PINTURA = Color(0xFF5E7A68)
    private val PINTURA_OSC = Color(0xFF3E5446)
    private val CAL = Color(0xFFE6E1D2)

    /** El sello de lacre: una torta de cera roja de borde chorreado, con la estrella de cinco puntas y su aro de puntos. */
    private fun DrawScope.surguch(c: Offset, r: Float, oscuro: Boolean, alfa: Float) {
        fun k(x: Color) = t(x, oscuro, false).copy(alpha = alfa)
        val gota = Path().apply {
            for (i in 0 until 14) {
                val a = (2 * PI * i / 14).toFloat()
                val rr = r * (if (i % 4 == 1) 1.12f else 0.94f + 0.06f * azar(i, 91))
                val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        translate(r * 0.07f, r * 0.1f) { drawPath(gota, Color.Black.copy(alpha = 0.3f * alfa)) }
        drawPath(gota, k(SURGUCH))
        drawCircle(k(mezcla(SURGUCH, Color.Black, 0.3f)), r * 0.72f, c, style = Stroke(r * 0.07f))
        for (i in 0 until 16) { val a = (2 * PI * i / 16).toFloat(); drawCircle(k(mezcla(SURGUCH, Color.Black, 0.35f)), r * 0.045f, Offset(c.x + cos(a) * r * 0.6f, c.y + sin(a) * r * 0.6f)) }
        drawPath(estrella(c, r * 0.44f, 5, 0.42f), k(mezcla(SURGUCH, ORO, 0.35f)))
        drawArc(k(Color.White.copy(alpha = 0.3f)), 200f, 60f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    /** La marca postal: dentada, con el cohete y la estrella sobre el cielo y el valor. */
    private fun DrawScope.marca(r: Rect, fondo: Color, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(SOBRE_LUZ), r.topLeft, r.size)
        val d = 0.7f * u
        var x = r.left + d * 1.5f
        while (x < r.right - d) { drawCircle(fondo, d, Offset(x, r.top)); drawCircle(fondo, d, Offset(x, r.bottom)); x += d * 3f }
        var y = r.top + d * 1.5f
        while (y < r.bottom - d) { drawCircle(fondo, d, Offset(r.left, y)); drawCircle(fondo, d, Offset(r.right, y)); y += d * 3f }
        val f = r.deflate(2 * u)
        drawRect(c(AZUL_IMPRESO), f.topLeft, f.size)
        val cohete = Offset(f.center.x, f.center.y)
        rotate(35f, cohete) {
            drawRoundRect(c(CREMA), Offset(cohete.x - f.width * 0.08f, cohete.y - f.height * 0.3f), Size(f.width * 0.16f, f.height * 0.5f), CornerRadius(f.width * 0.08f))
            drawPath(Path().apply { moveTo(cohete.x - f.width * 0.08f, cohete.y + f.height * 0.12f); lineTo(cohete.x - f.width * 0.18f, cohete.y + f.height * 0.25f); lineTo(cohete.x - f.width * 0.08f, cohete.y + f.height * 0.2f); close() }, c(ROJO))
            drawPath(Path().apply { moveTo(cohete.x + f.width * 0.08f, cohete.y + f.height * 0.12f); lineTo(cohete.x + f.width * 0.18f, cohete.y + f.height * 0.25f); lineTo(cohete.x + f.width * 0.08f, cohete.y + f.height * 0.2f); close() }, c(ROJO))
            drawLine(c(ORO), Offset(cohete.x, cohete.y + f.height * 0.22f), Offset(cohete.x, cohete.y + f.height * 0.38f), 1.2f * u)
        }
        drawPath(estrella(Offset(f.left + f.width * 0.25f, f.top + f.height * 0.22f), f.width * 0.1f, 5, 0.42f), c(ORO))
        textoEn(medidor, "4 к", Rect(f.left, f.bottom - f.height * 0.26f, f.right - 1 * u, f.bottom), letra, c(CREMA), 6f, minimo = 3f)
    }

    /**
     * El sobre de correo soviético, de papel gris crema, visto del lado de la solapa: la solapa en pico con «ПИСЬМО» impreso
     * en azul, la marca dentada del cohete con el matasellos redondo, y abajo el formulario impreso con sus renglones,
     * «Кому» (a quién va) y «Когда» (cuándo: las fechas), escritos en letra de molde; en la esquina, la plantilla de cifras del índice
     * postal. Lo cierra la torta roja de lacre con la estrella; las de al futuro, además, dos vueltas de bramante cuyos
     * cabos quedan presos bajo el lacre. Al abrirlo, el lacre se parte, la solapa se levanta y sube la carta. En modo
     * oscuro, el papel se hunde en la penumbra de la sala de lectura; la tinta sigue negra sobre crema.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(e.cuerpo.left + 2 * u, e.cuerpo.top + 1.5f * u, e.cuerpo.right - 2 * u, e.cuerpo.bottom - 3.5f * u)
        val l = b.left; val r = b.right; val top = b.top; val bot = b.bottom
        val W = b.width; val H = b.height; val cx = b.center.x
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(l + 1.5f * u, top + 2.5f * u), b.size)
        // Por dentro: el papel gris del forro, con su trama.
        drawRect(c(SOBRE_SOMBRA), b.topLeft, b.size)
        clipRect(l, top, r, top + H * 0.45f) { var x = l; while (x < r) { drawLine(c(mezcla(SOBRE_SOMBRA, TINTA, 0.25f)).copy(alpha = 0.4f), Offset(x, top), Offset(x, top + H * 0.45f), 0.6f * u); x += 2.2f * u } }
        val k = 1f - 2f * e.abrir.coerceIn(0f, 1f)
        val hondo = H * 0.42f
        val solapa = Path().apply { moveTo(l, top); lineTo(r, top); lineTo(cx, top + hondo); close() }
        if (k < 0f) scale(1f, k, Offset(cx, top)) {
            drawPath(solapa, c(mezcla(SOBRE, SOBRE_SOMBRA, 0.5f)))
            drawPath(solapa, c(SOBRE_SOMBRA), style = Stroke(0.8f * u))
        }
        // La carta.
        if (e.subir > 0f || e.abrir > 0.3f) {
            val h = hojaCo(e, 0.82f, 0.86f, 0.06f, 3 * u)
            papelCartaCo(h, e, 8 * u, 5.5f * u, 7 * u, oscuro)
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(h.left + 4 * u, h.top), Offset(h.left + 4 * u, h.bottom), 0.6f * u)
        }
        // El frente con su formulario impreso.
        val v = top + H * 0.28f
        val frente = Path().apply { moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f); lineTo(r, bot); lineTo(l, bot); close() }
        drawPath(frente, c(SOBRE))
        clipPath(frente) {
            for (j in 0 until 16) { val y = top + H * azar(e.semilla, j + 200); drawLine(c(SOBRE_SOMBRA).copy(alpha = 0.15f), Offset(l, y), Offset(r, y), 0.6f * u) }
            drawLine(c(SOBRE_SOMBRA), Offset(l, bot), Offset(cx - W * 0.1f, v + H * 0.06f), 0.8f * u)
            drawLine(c(SOBRE_SOMBRA), Offset(r, bot), Offset(cx + W * 0.1f, v + H * 0.06f), 0.8f * u)
            // La plantilla del índice postal: seis cifras de trazos.
            val ix = Rect(r - W * 0.36f, top + H * 0.36f, r - W * 0.05f, top + H * 0.52f)
            val cw = ix.width / 6
            val cifras = "%06d".format((e.semilla and 0x7fffffff) % 1000000)
            cifras.forEachIndexed { i, ch ->
                val d = Rect(ix.left + i * cw + cw * 0.15f, ix.top, ix.left + (i + 1) * cw - cw * 0.15f, ix.bottom)
                drawRect(c(AZUL_IMPRESO).copy(alpha = 0.25f), d.topLeft, d.size, style = Stroke(0.5f * u))
                textoEn(medidor, ch.toString(), d, letra, c(TINTA).copy(alpha = 0.75f), 8f, minimo = 4f)
            }
        }
        drawPath(Path().apply { moveTo(l, top + H * 0.1f); lineTo(cx, v); lineTo(r, top + H * 0.1f) }, c(SOBRE_SOMBRA), style = Stroke(1 * u))
        // Los renglones de «Кому» y «Когда».
        val y1 = top + H * 0.76f; val y2 = top + H * 0.9f
        listOf("Кому" to y1, "Когда" to y2).forEach { (et, y) ->
            textoEn(medidor, et, Rect(l + 4 * u, y - 9 * u, l + W * 0.15f, y), letra, c(AZUL_IMPRESO), 7f, minimo = 4f)
            drawLine(c(AZUL_IMPRESO).copy(alpha = 0.6f), Offset(l + W * 0.16f, y), Offset(r - 5 * u, y), 0.6f * u)
        }
        textoEn(medidor, e.linea1, Rect(l + W * 0.16f, y1 - H * 0.15f, r - 5 * u, y1 - 0.5f * u), letra, TINTA, 11.5f, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(l + W * 0.16f, y2 - H * 0.12f, r - 5 * u, y2 - 0.5f * u), letra, mezcla(TINTA, ROJO, 0.3f), 9f, minimo = 6f)
        // La solapa en pico, con «ПИСЬМО», la marca y el matasellos.
        if (k >= 0f) scale(1f, k.coerceAtLeast(0.001f), Offset(cx, top)) {
            translate(0f, 1.5f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.14f)) }
            drawPath(solapa, c(SOBRE_LUZ))
            clipPath(solapa) {
                textoEn(medidor, "ПИСЬМО", Rect(cx - W * 0.2f, top + H * 0.05f, cx + W * 0.2f, top + H * 0.17f), letra, c(AZUL_IMPRESO), 9f, espaciado = 1.5f, minimo = 4f)
                drawLine(c(ROJO), Offset(cx - W * 0.16f, top + H * 0.19f), Offset(cx + W * 0.16f, top + H * 0.19f), 1.2f * u)
            }
            drawPath(solapa, c(SOBRE_SOMBRA), style = Stroke(0.8f * u))
        }
        if (k > 0.6f) scale(1f, k, Offset(cx, top)) {
            val mh = H * 0.24f
            val mr = Rect(r - W * 0.06f - mh * 0.8f, top + H * 0.03f, r - W * 0.06f, top + H * 0.03f + mh)
            rotate(4f, mr.center) { marca(mr, c(SOBRE_LUZ), medidor, oscuro) }
            drawCircle(c(TINTA).copy(alpha = 0.45f), mh * 0.4f, Offset(mr.left, mr.center.y + mh * 0.1f), style = Stroke(0.7f * u))
            drawCircle(c(TINTA).copy(alpha = 0.35f), mh * 0.3f, Offset(mr.left, mr.center.y + mh * 0.1f), style = Stroke(0.5f * u))
            for (j in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.35f), Offset(mr.left - mh * 1.4f, mr.center.y + (j - 1) * 3 * u + mh * 0.1f), Offset(mr.left - mh * 0.45f, mr.center.y + (j - 1) * 3 * u + mh * 0.1f), 0.6f * u)
        }
        // El bramante, cuyos cabos quedan bajo el lacre.
        val y0 = top + hondo
        if (e.cordon > 0f) {
            listOf(-1f, 1f).forEach { s ->
                listOf(-1.4f * u, 1.4f * u).forEach { dy ->
                    val p = Path().apply { moveTo(if (s < 0) l - 2 * u else r + 2 * u, y0 + dy + s * 1.5f * u); lineTo(cx, y0 + dy * 0.4f) }
                    trazoParcialCo(p, e.cordon, c(BRAMANTE), 1.3f * u)
                }
            }
        }
        // El lacre.
        val selloR = H * 0.13f
        val selloC = Offset(cx, top + hondo * k)
        if (k >= 0f && e.alfaSello > 0f) {
            brilloSelloCo(selloC, selloR, c(LUZ), e.brillo)
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, e.escalaSello, Offset(-selloR * 0.3f * e.romper, selloC.y - y0 - selloR * 0.3f * e.romper),
                Offset(selloR * 0.25f * e.romper, selloR * 0.3f * e.romper), e.semilla) { surguch(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        } else if (e.alfaSello > 0f && e.romper > 0f) {
            selloPartidoCo(Offset(cx, y0), selloR, e.romper, 1f, Offset.Zero, Offset(selloR * 0.25f, selloR * 0.3f), e.semilla, conArriba = false) { surguch(Offset(cx, y0), selloR, oscuro, e.alfaSello) }
        }
    }

    override val cajaPrevia: Rect = Rect(0.16f, 0.24f, 0.84f, 0.39f)

    override val altoBuzon: Dp = 206.dp

    /**
     * Los buzones de la escalera del bloque de viviendas: un banco de diez cajas de chapa pintada de verde, en dos filas,
     * cada una con su número de piso en una tarjeta, su ranura, su cerradura y las tres filas de agujeros redondos por donde
     * se ve si hay carta: las que ya se pueden abrir se ven encendidas por dentro (el primer degradado), las selladas dejan
     * ver el bramante y el lacre rojo, y de las abiertas asoma la carta por la ranura. Encima, la tira de esmalte rojo
     * «ПИСЬМА»; debajo, tres plaquitas de esmalte con la cuenta de cada clase. La pared, de cal arriba y pintura verde abajo,
     * con su filete. En modo oscuro, la escalera de noche bajo la bombilla desnuda (el segundo degradado).
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun m(x: Color) = if (oscuro) mezcla(t(x, true, false), Color(0xFF080808), 0.4f) else t(x, false, false)
        // La pared: cal arriba, pintura abajo, con su filete.
        val zocalo = h * 0.55f
        drawRect(m(CAL), Offset.Zero, Size(w, zocalo))
        drawRect(m(PINTURA_OSC), Offset(0f, zocalo), Size(w, h - zocalo))
        drawRect(m(ROJO_OSC), Offset(0f, zocalo - 1.5f * u), Size(w, 3 * u))
        if (oscuro) {
            mancha(Offset(w * 0.5f, 0f), w * 0.6f, LUZ.copy(alpha = 0.22f))
            drawLine(c(TINTA), Offset(w * 0.5f, 0f), Offset(w * 0.5f, 5 * u), 1 * u)
            drawCircle(LUZ, 3 * u, Offset(w * 0.5f, 8 * u))
        }
        // La tira de esmalte «ПИСЬМА».
        val tira = Rect(w * 0.34f, 6 * u, w * 0.66f, 22 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), tira.topLeft + Offset(1 * u, 1.5f * u), tira.size, CornerRadius(3 * u))
        drawRoundRect(c(ROJO), tira.topLeft, tira.size, CornerRadius(3 * u))
        drawRoundRect(c(CREMA), tira.deflate(1.5f * u).topLeft, tira.deflate(1.5f * u).size, CornerRadius(2 * u), style = Stroke(0.7f * u))
        textoEn(medidor, "ПИСЬМА", tira.deflate(2 * u), letra, c(CREMA), 11f, espaciado = 2f, minimo = 6f)
        // El banco de buzones.
        val banco = Rect(w * 0.06f, 28 * u, w * 0.94f, h - 40 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), banco.topLeft + Offset(2 * u, 3 * u), banco.size)
        drawRect(c(PINTURA_OSC), banco.topLeft, banco.size)
        val cols = 5; val gap = 2.5f * u
        val bw = (banco.width - gap * (cols + 1)) / cols
        val bh = (banco.height - gap * 3) / 2
        val cartas = cartasBuzonCo(e, 10)
        for (f in 0 until 2) for (k in 0 until cols) {
            val i = f * cols + k
            val tipo = cartas.getOrNull(i)
            val caja = Rect(banco.left + gap + k * (bw + gap), banco.top + gap + f * (bh + gap), banco.left + gap + k * (bw + gap) + bw, banco.top + gap + f * (bh + gap) + bh)
            // La carta abierta asomando por la ranura.
            val ranura = Rect(caja.left + bw * 0.15f, caja.top + bh * 0.1f, caja.right - bw * 0.15f, caja.top + bh * 0.1f + 3 * u)
            if (tipo == 2) {
                drawRect(c(SOBRE), Offset(ranura.left + 3 * u, ranura.top - 11 * u), Size(ranura.width - 6 * u, 13 * u))
                drawLine(c(AZUL_IMPRESO).copy(alpha = 0.5f), Offset(ranura.left + 6 * u, ranura.top - 7 * u), Offset(ranura.right - 8 * u, ranura.top - 7 * u), 0.6f * u)
            }
            drawRect(c(PINTURA), caja.topLeft, caja.size)
            bisel(caja, c(mezcla(PINTURA, Color.White, 0.25f)).copy(alpha = 0.7f), c(PINTURA_OSC), 1 * u)
            drawRect(c(TINTA), ranura.topLeft, ranura.size)
            if (tipo == 2) drawRect(c(SOBRE), Offset(ranura.left + 3 * u, ranura.top), Size(ranura.width - 6 * u, ranura.height * 0.6f))
            // La tarjeta con el número.
            val tj = Rect(caja.left + bw * 0.18f, ranura.bottom + 3 * u, caja.left + bw * 0.62f, ranura.bottom + 12 * u)
            drawRect(c(CREMA), tj.topLeft, tj.size)
            drawRect(c(TINTA).copy(alpha = 0.4f), tj.topLeft, tj.size, style = Stroke(0.5f * u))
            textoEn(medidor, "${i + 1}", tj, letra, c(TINTA), 7.5f, minimo = 4f)
            // La cerradura.
            val ce = Offset(caja.right - bw * 0.18f, tj.center.y)
            drawCircle(c(ALUMINIO_URSS), 2.6f * u, ce)
            drawLine(c(TINTA), ce - Offset(0f, 1.2f * u), ce + Offset(0f, 1.2f * u), 0.8f * u)
            if (tipo == 0) mancha(Offset(caja.center.x, caja.bottom - bh * 0.3f), bw * 0.55f, c(LUZ).copy(alpha = 0.4f))
            // Los agujeros: tres filas, por donde se ve lo que hay dentro.
            val ah = Rect(caja.left + bw * 0.14f, tj.bottom + 4 * u, caja.right - bw * 0.14f, caja.bottom - 4 * u)
            val nx = 4; val ny = 3
            for (a in 0 until ny) for (b in 0 until nx) {
                val p = Offset(ah.left + ah.width * (b + 0.5f) / nx, ah.top + ah.height * (a + 0.5f) / ny)
                val rr = minOf(ah.width / nx, ah.height / ny) * 0.3f
                val dentro = when (tipo) {
                    null -> c(TINTA)
                    0 -> mezcla(c(SOBRE), LUZ, 0.5f)
                    1 -> if (a == 1 && b == 1) c(SURGUCH) else if (b == 2) c(BRAMANTE) else c(SOBRE)
                    else -> if (a == 0) c(TINTA) else c(SOBRE)
                }
                drawCircle(dentro, rr, p)
                drawArc(Color.Black.copy(alpha = 0.4f), 180f, 180f, false, Offset(p.x - rr, p.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.6f * u))
            }
        }
        // Las tres plaquitas de esmalte con la cuenta.
        val cuentas = listOf("POR ABRIR" to e.listas, "SELLADAS" to e.selladas, "ABIERTAS" to e.abiertas)
        val pw = minOf(w * 0.27f, 104 * u); val gp = (w - pw * 3) / 4
        cuentas.forEachIndexed { i, (txt, n) ->
            val pl = Rect(gp + i * (pw + gp), h - 33 * u, gp + i * (pw + gp) + pw, h - 9 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(1 * u, 1.5f * u), pl.size, CornerRadius(3 * u))
            drawRoundRect(c(CREMA), pl.topLeft, pl.size, CornerRadius(3 * u))
            drawRoundRect(c(if (i == 0) ROJO else TINTA), pl.deflate(1.5f * u).topLeft, pl.deflate(1.5f * u).size, CornerRadius(2 * u), style = Stroke(0.8f * u))
            listOf(pl.left + 4 * u, pl.right - 4 * u).forEach { x -> drawCircle(c(ALUMINIO_URSS), 1.2f * u, Offset(x, pl.center.y)) }
            textoEn(medidor, txt, Rect(pl.left + 8 * u, pl.top + 2 * u, pl.left + pl.width * 0.66f, pl.bottom - 2 * u), letra, c(TINTA), 8f, minimo = 4.5f)
            textoEn(medidor, "$n", Rect(pl.left + pl.width * 0.64f, pl.top + 1 * u, pl.right - 7 * u, pl.bottom - 1 * u), letra, c(if (i == 0 && n > 0) ROJO else TINTA), 14f, minimo = 6f)
        }
    }
}

/**
 * Una fecha larga partida en dos renglones por el « · » que deja las dos mitades más parejas, para que quepa en la tapa sin
 * achicarse demasiado; si es corta (hasta [largo] letras) o no tiene separador, va entera en el primero.
 */
private fun partirFecha(f: String, largo: Int): Pair<String, String?> {
    if (f.length <= largo) return f to null
    var mejor = -1
    var i = f.indexOf(" · ")
    while (i > 0) {
        if (mejor < 0 || maxOf(i, f.length - i - 3) < maxOf(mejor, f.length - mejor - 3)) mejor = i
        i = f.indexOf(" · ", i + 3)
    }
    if (mejor <= 0) return f to null
    return f.substring(0, mejor) to f.substring(mejor + 3)
}

// =====================================================================
//  Ayudantes de la página abierta (solo de este archivo)
// =====================================================================

/**
 * La penumbra del modo oscuro sobre un papel claro: un velo negro en elipse, leve en el centro de lectura (corrido un poco
 * hacia [fuente]) y muy hondo en las orillas, y el resplandor de la luz propia del tema ([luz], con [fuerza]) que sale de
 * [fuente]. En conjunto oscurece como un velo de ~30 %, pero el centro, donde va el texto, queda como papel apenas
 * atenuado (~18 %) y la tinta oscura se sigue leyendo. Usa los dos degradados de la página.
 */
private fun DrawScope.penumbraCon(fuente: Offset, luz: Color, fuerza: Float = 0.16f) {
    val w = size.width; val h = size.height
    val c = Offset(w * 0.5f + (fuente.x - w * 0.5f) * 0.16f, h * 0.44f + (fuente.y - h * 0.44f) * 0.1f)
    val rx = w * 0.8f; val ry = h * 0.64f
    val k = ry / rx
    // El círculo de radio rx, estirado en alto, se vuelve la elipse rx × ry.
    scale(1f, k, c) {
        drawRect(Brush.radialGradient(0f to Color.Black.copy(alpha = 0.17f), 0.45f to Color.Black.copy(alpha = 0.22f),
            0.78f to Color.Black.copy(alpha = 0.4f), 1f to Color.Black.copy(alpha = 0.7f), center = c, radius = rx),
            Offset(0f, c.y - c.y / k), Size(w, h / k))
    }
    drawRect(Brush.radialGradient(0f to luz.copy(alpha = fuerza), 0.45f to luz.copy(alpha = fuerza * 0.4f), 1f to luz.copy(alpha = 0f),
        center = fuente, radius = maxOf(w, h) * 0.72f))
}

/** Un haz de luz: una cuña lisa desde [o] hacia el ángulo [a] (grados), de [abre] grados de abertura y [largo] de alcance. */
private fun DrawScope.hazCon(o: Offset, a: Float, abre: Float, largo: Float, color: Color) {
    val r1 = Math.toRadians((a - abre / 2).toDouble()); val r2 = Math.toRadians((a + abre / 2).toDouble())
    drawPath(Path().apply {
        moveTo(o.x, o.y)
        lineTo(o.x + largo * cos(r1).toFloat(), o.y + largo * sin(r1).toFloat())
        lineTo(o.x + largo * cos(r2).toFloat(), o.y + largo * sin(r2).toFloat()); close()
    }, color)
}

/** El papel tal como se ve: en modo oscuro, un papel claro queda algo hundido (para las placas del encabezado). */
private fun papelVistoCon(p: Pagina, oscuro: Boolean): Color = when {
    p.oscura -> p.color
    oscuro -> mezcla(p.color, Color.Black, 0.22f)
    else -> p.color
}

/**
 * El fondo de la barra de arriba: de día, el papel apenas cubierto (se adivina su textura); en modo oscuro, el papel algo
 * hundido (no la penumbra de la orilla), para que «atrás» y la cuenta de palabras se lean; sobre un papel oscuro, un poco
 * más claro que él.
 */
private fun DrawScope.fondoBarraCon(p: Pagina, oscuro: Boolean) = when {
    p.oscura -> drawRect(mezcla(p.color, p.tinta, 0.05f).copy(alpha = 0.94f))
    oscuro -> drawRect(mezcla(p.color, Color.Black, 0.22f))
    else -> drawRect(p.color.copy(alpha = 0.6f))
}

/** El color de una pieza del botón de guardar: en modo oscuro o de noche, apenas hundido, para que su letra siga leyéndose. */
private fun tonoBotonCon(c: Color, oscuro: Boolean, noche: Boolean): Color = if (oscuro || noche) mezcla(c, Color.Black, 0.16f) else c

/** Una luna creciente: el disco de [r] en [c] menos otro corrido hacia la derecha y arriba (no hace falta saber el fondo). */
private fun DrawScope.crecienteCon(c: Offset, r: Float, color: Color) {
    val hueco = Path().apply { addOval(Rect(Offset(c.x + r * 0.45f, c.y - r * 0.28f), r * 0.86f)) }
    clipPath(hueco, ClipOp.Difference) { drawCircle(color, r, c) }
}

/**
 * Un renglón alineado a la izquierda desde [x], centrado en alto en [y]: si pasa de [ancho], se achica (hasta [minimo] sp) y
 * luego se corta. Devuelve la x donde termina (para poner el cursor detrás).
 */
private fun DrawScope.renglonCon(medidor: TextMeasurer, texto: String, x: Float, y: Float, ancho: Float, letra: FontFamily, color: Color,
                                 tam: Float, peso: FontWeight = FontWeight.Normal, minimo: Float = 6f): Float {
    if (texto.isBlank() || ancho <= 1f) return x
    var estilo = TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, color = color, fontWeight = peso)
    var capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
    if (capa.size.width > ancho) {
        val nuevo = maxOf(minimo, tam * ancho / capa.size.width)
        estilo = estilo.copy(fontSize = nuevo.sp, lineHeight = (nuevo * 1.05f).sp)
        capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxWidth = ceil(ancho).toInt().coerceAtLeast(1)))
    }
    drawText(capa, topLeft = Offset(x, y - capa.size.height / 2f))
    return x + capa.size.width
}

/**
 * La fecha del encabezado, que es la protagonista: en un renglón si cabe a [tam] (o poco menos); si no, partida en dos
 * renglones por el « · » más parejo, cada uno en una mitad de [r] y un poco más chico. Sin separador, se achica en uno.
 */
private fun DrawScope.fechaCon(medidor: TextMeasurer, f: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                               peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f) {
    val ancho = medidor.measure(f, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width
    val (a, b) = partirFecha(f, 0)
    if (ancho <= r.width * 1.12f || b == null) {
        textoEn(medidor, f, r, letra, color, tam, peso = peso, espaciado = espaciado, minimo = 8f)
        return
    }
    val m = r.top + r.height / 2
    textoEn(medidor, a, Rect(r.left, r.top, r.right, m), letra, color, tam * 0.8f, peso = peso, espaciado = espaciado, minimo = 8f)
    textoEn(medidor, b, Rect(r.left, m, r.right, r.bottom), letra, color, tam * 0.8f, peso = peso, espaciado = espaciado, minimo = 8f)
}

// =====================================================================
//  Ayudantes de la libreta de momentos (solo de este archivo)
// =====================================================================

/**
 * Cuánto engorda la libreta con sus hojas, de 0 (una hoja) a 1 (240 o más): rápido al principio y cada vez más despacio, para
 * que se note la diferencia entre 1 y 20 hojas sin que con 200 se salga de la escena.
 */
private fun llenoLibreta(hojas: Int): Float = kotlin.math.sqrt((hojas.coerceIn(1, 240) - 1) / 239f)

/** La apertura en dos tiempos: la primera parte ([corte] de 0 a 1) suelta el cierre; el resto abre. Devuelve (cierre, abre). */
private fun tiemposLibreta(a: Float, corte: Float = 0.3f): Pair<Float, Float> {
    val x = a.coerceIn(0f, 1f)
    return (x / corte).coerceIn(0f, 1f) to ((x - corte * 0.5f) / (1f - corte * 0.5f)).coerceIn(0f, 1f)
}

/** El canto de las hojas de una libreta tumbada: la franja de [grosor] bajo [r] (y un poco a la derecha), con sus hojas. */
private fun DrawScope.cantoLibreta(r: Rect, grosor: Float, papel: Color, raya: Color, radio: Float = 0f) {
    val u = density
    if (grosor <= 0.3f * u) return
    val lado = grosor * 0.35f
    drawRoundRect(papel, Offset(r.left + 2 * u, r.top + lado), Size(r.width - 2 * u + lado, r.height + grosor - lado), CornerRadius(radio))
    var y = r.bottom + 1.3f * u
    while (y < r.bottom + grosor - 0.8f * u) { drawLine(raya, Offset(r.left + 3 * u, y), Offset(r.right + lado * (y - r.bottom) / grosor, y), 0.5f * u); y += 1.5f * u }
    var x = r.right + 1.2f * u
    while (x < r.right + lado - 0.5f * u) { drawLine(raya, Offset(x, r.top + lado + 2 * u), Offset(x, r.bottom + (x - r.right) / lado * grosor), 0.5f * u); x += 1.5f * u }
}

// =====================================================================
//  Ayudantes de las cartas de la Edad Contemporánea
// =====================================================================

/**
 * Qué cartas se ven en un buzón, hasta [tope]: 0 las listas para abrir, 1 las selladas y 2 las abiertas, en ese orden. Si hay
 * más que [tope], se reparten en proporción, pero cada clase que haya conserva al menos una.
 */
private fun cartasBuzonCo(e: EstadoBuzon, tope: Int): List<Int> {
    val n = intArrayOf(e.listas.coerceAtLeast(0), e.selladas.coerceAtLeast(0), e.abiertas.coerceAtLeast(0))
    val total = n.sum()
    if (total <= tope) return (0..2).flatMap { k -> List(n[k]) { k } }
    val v = IntArray(3) { k -> if (n[k] > 0) maxOf(1, n[k] * tope / total) else 0 }
    while (v.sum() > tope) { val k = (0..2).filter { v[it] > 1 }.maxByOrNull { v[it] } ?: break; v[k]-- }
    while (v.sum() < tope) { val k = (0..2).filter { v[it] < n[it] }.maxByOrNull { n[it] - v[it] } ?: break; v[k]++ }
    return (0..2).flatMap { k -> List(v[k]) { k } }
}

/**
 * Dónde va la hoja de la carta: centrada, de [ancho] y [alto] (fracciones del cuerpo). Guardada, su borde de arriba queda a
 * [dentro] del cuerpo (fracción de su alto, desde arriba); con [EstadoCarta.subir] en 1 sube hasta [margen] px del borde de
 * arriba del lienzo (en la rejilla, con subir 0.5, queda a medio camino).
 */
private fun hojaCo(e: EstadoCarta, ancho: Float, alto: Float, dentro: Float, margen: Float): Rect {
    val c = e.cuerpo
    val w = c.width * ancho
    val arriba0 = c.top + c.height * dentro
    val arriba = arriba0 - e.subir.coerceIn(0f, 1f) * (arriba0 - margen).coerceAtLeast(0f)
    return Rect(c.center.x - w / 2, arriba, c.center.x + w / 2, arriba + c.height * alto)
}

/**
 * La hoja de la carta en [r]: el papel [EstadoCarta.hoja] (en modo oscuro, un poco en penumbra) con su sombra y sus renglones
 * [EstadoCarta.renglones], desde [desde] px bajo el borde de arriba, cada [paso] px, con [margen] px a los lados.
 */
private fun DrawScope.papelCartaCo(r: Rect, e: EstadoCarta, desde: Float, paso: Float, margen: Float, oscuro: Boolean, radio: Float = 0f) {
    val u = density
    val papel = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
    drawRoundRect(Color.Black.copy(alpha = 0.18f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size, CornerRadius(radio))
    drawRoundRect(papel, r.topLeft, r.size, CornerRadius(radio))
    var y = r.top + desde; var i = 0
    while (y < r.bottom - paso * 0.5f && i < 24) {
        drawLine(e.renglones, Offset(r.left + margen, y), Offset(r.right - margen - (if (i % 4 == 3) r.width * 0.25f else 0f), y), 0.8f * u)
        y += paso; i++
    }
}

/**
 * Un sello que se parte: con [romper] en 0 se dibuja entero (a [escala]); si no, una grieta en zigzag lo corta en una mitad de
 * arriba (corrida [arriba], que se va con la solapa o la tapa, y que no se dibuja si [conArriba] es falso) y otra de abajo
 * (corrida [abajo], que se queda), cada una un poco girada. [pinta] dibuja el sello entero, centrado en [c], de radio [r].
 */
private fun DrawScope.selloPartidoCo(c: Offset, r: Float, romper: Float, escala: Float, arriba: Offset, abajo: Offset, semilla: Int,
                                            conArriba: Boolean = true, pinta: DrawScope.() -> Unit) {
    if (romper <= 0f) { scale(escala, escala, c) { pinta() }; return }
    val zig = (0..7).map { k -> Offset(c.x - r * 1.8f + r * 3.6f * k / 7f, c.y + r * (if (k % 2 == 0) 0.2f else -0.16f) * (0.6f + 0.8f * azar(semilla, k))) }
    fun mitad(y: Float) = Path().apply { zig.forEachIndexed { k, p -> if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; lineTo(c.x + r * 1.8f, y); lineTo(c.x - r * 1.8f, y); close() }
    val sup = mitad(c.y - r * 2f); val inf = mitad(c.y + r * 2f)
    if (conArriba) translate(arriba.x, arriba.y) { rotate(-9f * romper, c) { clipPath(sup) { pinta() } } }
    translate(abajo.x, abajo.y) { rotate(7f * romper, c) { clipPath(inf) { pinta() } } }
}

/** El brillo del sello de la carta que ya se puede abrir: un halo (un degradado) y cuatro destellos finos. */
private fun DrawScope.brilloSelloCo(c: Offset, r: Float, color: Color, brillo: Float) {
    if (brillo <= 0f) return
    val u = density
    mancha(c, r * 2.6f, color.copy(alpha = 0.55f * brillo))
    for (k in 0 until 4) {
        val a = (PI / 4 + k * PI / 2).toFloat()
        val d0 = r * 1.15f; val d1 = r * (1.6f + 0.5f * brillo)
        drawLine(color.copy(alpha = 0.8f * brillo), Offset(c.x + cos(a) * d0, c.y + sin(a) * d0), Offset(c.x + cos(a) * d1, c.y + sin(a) * d1), 1.1f * u)
    }
}

/** Un trazo dibujado hasta la fracción [f] de su largo (un cordón que se va atando). */
private fun DrawScope.trazoParcialCo(p: Path, f: Float, color: Color, grosor: Float, cap: StrokeCap = StrokeCap.Round) {
    if (f <= 0f) return
    if (f >= 1f) { drawPath(p, color, style = Stroke(grosor, cap = cap)); return }
    val m = PathMeasure(); m.setPath(p, false)
    val parte = Path(); m.getSegment(0f, m.length * f, parte, true)
    drawPath(parte, color, style = Stroke(grosor, cap = cap))
}
