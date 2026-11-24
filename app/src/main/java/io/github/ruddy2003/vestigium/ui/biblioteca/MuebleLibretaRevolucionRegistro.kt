package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El registro del regente ([MuebleLibretaRevolucion]), en vivo: de pie en el tablero, apoyado en el reborde. Media
 * encuadernación: las tapas forradas de papel verde cardenillo (liso, con su roce), el lomo redondo y las puntas de becerro, los
 * nervios del lomo; el grueso de las hojas asoma a la derecha (el corte delantero) y abajo (el pie), salpicado de ocre rojo y
 * entre el canto de las dos tapas; le salen papeles por la cabeza (uno cada 30 páginas) y la cinta de seda cuelga por el
 * reborde. La etiqueta de papel impresa lleva el nombre y la cuenta. Al abrirse, la tapa gira sobre el lomo y se ve la primera
 * página rayada, con su margen rojo y los renglones a pluma (rayas, sin letras).
 */
internal object RegistroLibretaRevolucion {
    private val P = PaletaRevolucion
    /** El papel verde de las tapas, el becerro del lomo y las puntas, el ocre rojo del salpicado de los cantos y la cinta. */
    private val CARTON = Color(0xFF52786A)
    private val BECERRO = Color(0xFF7A4A2C)
    private val BECERRO_OSC = Color(0xFF4A2A18)
    private val SALPICADO = Color(0xFFA0462E)
    private val CINTA = Color(0xFFB04A34)

    fun DrawScope.registro(e: EstadoLibreta, s: SitioLibretaRevolucion, a: AireRevolucion, medidor: TextMeasurer) {
        val U = s.U
        val r = s.registro
        val ab = e.abierto.coerceIn(0f, 1f)
        val gx = s.gx; val gy = s.gy
        // La sombra del registro en el tablero: de día a la derecha (la ventana está a la izquierda); de noche, a la izquierda.
        val sx = if (a.oscuro) -10f * U else 11f * U
        clipRect(s.izq, s.galeriaAbajo, s.der, s.reborde) {
            drawRect(a.sombra(0.12f), Offset(r.left + sx - 3f * U, r.top + 2f * U), Size(r.width + gx + 6f * U, s.reborde - r.top))
            drawRect(a.sombra(0.22f), Offset(r.left + sx, r.top + 5f * U), Size(r.width + gx, s.reborde - r.top))
        }
        // Los papeles que asoman por la cabeza (salen del bloque de las hojas, detrás de la tapa): uno cada 30 páginas.
        val papeles = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until papeles) {
            val px = r.left + gx * 0.5f + r.width * (0.2f + 0.62f * azar(e.semilla, 50 + j))
            val alto = (6f + 7f * azar(e.semilla, 70 + j)) * U
            val base = r.top + gy * 0.5f
            rotate(-7f + 14f * azar(e.semilla, 60 + j), Offset(px, base)) {
                val pr = Rect(px - 9f * U, base - alto, px + 9f * U, base + 4f * U)
                val k = MuebleLibretaRevolucion.cerca(pr.center, s, a)
                drawRect(a.sombra(0.25f), Offset(pr.left + 1.2f * U, pr.top + 1.2f * U), pr.size)
                drawRect(a.dia(if (j % 3 == 2) P.TOSTADO else P.PAPEL, 0f, k), pr.topLeft, pr.size)
                drawLine(a.dia(P.TINTA, 0f, 0.5f).copy(alpha = 0.35f), Offset(pr.left + 3f * U, pr.top + 3f * U), Offset(pr.right - 4f * U, pr.top + 3f * U), 0.8f * U)
            }
        }
        grueso(s, a, r)
        // La primera página, que se ve al abrir.
        if (ab > 0.01f) pagina(e, s, a, r)
        // La tapa, que gira sobre el lomo al abrirse; de día, los paños de la ventana la pintan corridos (está más adelante que el
        // tablero). De noche la luna se queda en la pared y en el filo del lomo: en el becerro se leía como una chapa.
        val sxa = 1f - 0.94f * ab
        scale(sxa, 1f, Offset(r.left, r.center.y)) {
            tapa(e, s, a, r, 0f, medidor)
            if (ab < 0.05f && !a.oscuro) {
                val d = s.pano.tapa(s.g)
                clipPath(s.pano.forma(d.x, d.y)) { tapa(e, s, a, r, s.pano.fuerza(a), medidor) }
            }
            if (ab > 0f) drawRect(a.sombra(0.5f * ab), r.topLeft, r.size)
        }
        if (ab > 0.01f) {
            val borde = r.left + r.width * sxa
            for (j in 0 until 3) drawRect(a.sombra(0.12f * (3 - j) / 3f), Offset(borde, r.top), Size((3f + 4f * j) * U, r.height))
        }
        cinta(s, a, r)
    }

    /** El grueso del registro: el corte delantero a la derecha y el pie abajo, con el canto de las dos tapas y el lomo redondo. */
    private fun DrawScope.grueso(s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect) {
        val U = s.U
        val gx = s.gx; val gy = s.gy
        val kd = MuebleLibretaRevolucion.cerca(Offset(r.right + gx, r.center.y), s, a)
        val kp = MuebleLibretaRevolucion.cerca(Offset(r.center.x, r.bottom), s, a)
        // La tapa de atrás, entera detrás (asoma su canto al fondo de las dos caras).
        drawPath(poligonoRevolucion(r.right, r.top, r.right + gx, r.top + gy, r.right + gx, r.bottom + gy, r.left + gx, r.bottom + gy, r.left, r.bottom),
            a.dia(mezcla(CARTON, BECERRO_OSC, 0.5f), -0.15f, (kd + kp) / 2))
        // Las hojas, entre las dos tapas (el corte salpicado de ocre rojo): la cara de la derecha mira lejos de la ventana (de
        // noche, a la vela); la de abajo, de frente.
        val t0 = 0.16f; val t1 = 0.84f
        val hojasDer = poligonoRevolucion(r.right + gx * t0, r.top + gy * t0 + 2f * U, r.right + gx * t1, r.top + gy * t1 + 2f * U,
            r.right + gx * t1, r.bottom + gy * t1 - 1f * U, r.right + gx * t0, r.bottom + gy * t0 - 1f * U)
        val hojasPie = poligonoRevolucion(r.left + 4f * U + gx * t0, r.bottom + gy * t0, r.right - 1f * U + gx * t0, r.bottom + gy * t0,
            r.right - 1f * U + gx * t1, r.bottom + gy * t1, r.left + 4f * U + gx * t1, r.bottom + gy * t1)
        val corte = mezcla(P.PAPEL, SALPICADO, 0.22f)
        drawPath(hojasDer, a.dia(corte, if (a.oscuro) 0f else -0.16f, kd))
        drawPath(hojasPie, a.dia(corte, -0.06f, kp))
        // Las capas de las hojas: rayas finas paralelas al canto, y el salpicado, en puntos sueltos.
        val capas = Path()
        val n = (s.g / (1.7f * U)).toInt().coerceIn(2, 14)
        for (i in 1 until n) {
            val t = t0 + (t1 - t0) * i / n
            capas.moveTo(r.right + gx * t, r.top + gy * t + 2f * U); capas.lineTo(r.right + gx * t, r.bottom + gy * t - 1f * U)
            capas.moveTo(r.left + 4f * U + gx * t, r.bottom + gy * t); capas.lineTo(r.right - 1f * U + gx * t, r.bottom + gy * t)
        }
        drawPath(capas, a.sombra(0.16f), style = Stroke(0.5f * U))
        val motas = Path()
        for (j in 0 until 26) {
            val t = t0 + (t1 - t0) * azar(9700, j)
            val p = if (j % 2 == 0) Offset(r.right + gx * t, r.top + (r.height - 4f * U) * azar(9701, j) + gy * t + 2f * U)
            else Offset(r.left + 6f * U + (r.width - 8f * U) * azar(9701, j) + gx * t, r.bottom + gy * t)
            motas.addOval(Rect(p.x - 0.5f * U, p.y - 0.5f * U, p.x + 0.5f * U, p.y + 0.5f * U))
        }
        drawPath(motas, a.dia(SALPICADO, -0.2f, (kd + kp) / 2).copy(alpha = 0.7f))
        // El canto de la tapa de delante (el primer trecho del grueso): becerro en las puntas y el lomo, papel verde en medio.
        val tb = 0.14f
        drawPath(poligonoRevolucion(r.right, r.top, r.right + gx * tb, r.top + gy * tb, r.right + gx * tb, r.bottom + gy * tb, r.right, r.bottom), a.dia(BECERRO, -0.1f, kd))
        drawPath(poligonoRevolucion(r.left, r.bottom, r.right, r.bottom, r.right + gx * tb, r.bottom + gy * tb, r.left + gx * tb, r.bottom + gy * tb), a.dia(BECERRO, -0.2f, kp))
        // El lomo redondo al pie: la cofia de becerro que da la vuelta de la tapa de delante a la de atrás.
        val cofia = Path().apply {
            moveTo(r.left + 2f * U, r.bottom); lineTo(r.left + 17f * U, r.bottom)
            lineTo(r.left + 17f * U + gx, r.bottom + gy); lineTo(r.left + 2f * U + gx, r.bottom + gy)
            quadraticTo(r.left - 1f * U + gx * 0.4f, r.bottom + gy * 0.7f, r.left + 2f * U, r.bottom); close()
        }
        drawPath(cofia, a.dia(BECERRO, -0.15f, kp))
        drawPath(cofia, a.sombra(0.3f), style = Stroke(0.6f * U))
        // El filo de la vela en el corte (de noche, la vela está arriba a la derecha) y la sombra del registro en el reborde.
        if (a.oscuro) drawLine(a.filo((0.7f * kd).coerceAtMost(0.6f)), Offset(r.right + gx, r.top + gy), Offset(r.right + gx, r.bottom + gy), 1.2f * U)
        drawRect(a.sombra(0.45f), Offset(r.left - 3f * U, s.reborde), Size(r.width + gx + 5f * U, 1.8f * U))
    }

    /** La tapa en [r]: con [luz] mayor que 0, la misma tapa bajo los paños de la ventana (se pinta dentro de su recorte). */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect, luz: Float, medidor: TextMeasurer) {
        val U = s.U
        fun pincel(col: Color, f: Float): Brush =
            if (a.oscuro || luz > 0f) MuebleLibretaRevolucion.pincelGuardado(col, f, s, a, luz, local = 0.5f)
            else Brush.linearGradient(listOf(a.dia(col, f + 0.1f), a.dia(col, f - 0.1f)), r.topLeft, r.bottomRight)
        val lomo = 17f * U
        val alto = r.height
        // La silueta: el lomo redondo abomba a la izquierda; las esquinas de la derecha, apenas romas.
        val forma = Path().apply {
            moveTo(r.left + 4f * U, r.top); lineTo(r.right - 2f * U, r.top)
            quadraticTo(r.right, r.top, r.right, r.top + 2f * U); lineTo(r.right, r.bottom - 2f * U)
            quadraticTo(r.right, r.bottom, r.right - 2f * U, r.bottom); lineTo(r.left + 4f * U, r.bottom)
            cubicTo(r.left - 1f * U, r.bottom, r.left - 4f * U, r.bottom - alto * 0.25f, r.left - 4f * U, r.center.y)
            cubicTo(r.left - 4f * U, r.top + alto * 0.25f, r.left - 1f * U, r.top, r.left + 4f * U, r.top); close()
        }
        clipPath(forma) {
            // El papel verde de la tapa: liso, con su luz, y el roce de las manos (más claro, se ve la fibra) abajo a la derecha.
            drawRect(pincel(CARTON, 0f), Offset(r.left - 5f * U, r.top), Size(r.width + 6f * U, alto))
            // Las puntas de becerro, arriba y abajo a la derecha, con la vuelta del cuero.
            val p = 30f * U
            val puntas = Path().apply {
                moveTo(r.right - p, r.top); lineTo(r.right + 1f, r.top); lineTo(r.right + 1f, r.top + p); close()
                moveTo(r.right - p, r.bottom); lineTo(r.right + 1f, r.bottom); lineTo(r.right + 1f, r.bottom - p); close()
            }
            drawPath(puntas, pincel(BECERRO, 0f))
            val filos = Path().apply {
                moveTo(r.right - p, r.top); lineTo(r.right, r.top + p)
                moveTo(r.right - p, r.bottom); lineTo(r.right, r.bottom - p)
            }
            drawPath(filos, a.sombra(0.45f), style = Stroke(1.2f * U))
            drawPath(Path().apply { addPath(filos, Offset(-1.4f * U, 0f)) }, a.claro(0.3f), style = Stroke(0.8f * U))
            // El lomo de becerro, redondo: la luz de la ventana en la curva de la izquierda, la sombra junto a la charnela.
            drawRect(pincel(BECERRO, 0f), Offset(r.left - 5f * U, r.top), Size(lomo + 5f * U, alto))
            drawRect(a.claro(0.28f), Offset(r.left - 2.6f * U, r.top), Size(3.4f * U, alto))
            drawRect(a.sombra(0.22f), Offset(r.left + lomo - 6f * U, r.top), Size(6f * U, alto))
            // La charnela: el surco donde el lomo se dobla hacia la tapa.
            drawRect(a.sombra(0.5f), Offset(r.left + lomo, r.top), Size(1.4f * U, alto))
            drawRect(a.claro(0.3f), Offset(r.left + lomo + 1.4f * U, r.top), Size(1f * U, alto))
            // Los nervios del lomo: cinco lomos de cuerda bajo el becerro, con su luz arriba y su sombra debajo.
            val nervios = Path(); val luces = Path(); val sombras = Path()
            listOf(0.13f, 0.3f, 0.5f, 0.7f, 0.87f).forEach { f ->
                val y = r.top + alto * f
                nervios.addRect(Rect(r.left - 5f * U, y - 1.8f * U, r.left + lomo - 2f * U, y + 1.8f * U))
                luces.addRect(Rect(r.left - 5f * U, y - 1.8f * U, r.left + lomo - 2f * U, y - 0.9f * U))
                sombras.addRect(Rect(r.left - 5f * U, y + 1.8f * U, r.left + lomo - 2f * U, y + 3.2f * U))
            }
            drawPath(nervios, pincel(BECERRO, 0.08f))
            drawPath(luces, a.claro(0.4f)); drawPath(sombras, a.sombra(0.35f))
        }
        // Los cantos: la luz de la ventana arriba; la sombra abajo y a la derecha.
        drawLine(a.claro(0.5f), Offset(r.left + 4f * U, r.top + 0.5f * U), Offset(r.right - 2f * U, r.top + 0.5f * U), 1f * U)
        drawLine(a.sombra(0.35f), Offset(r.left + 4f * U, r.bottom - 0.5f * U), Offset(r.right - 2f * U, r.bottom - 0.5f * U), 1f * U)
        // De noche, el filo de la vela en el canto derecho y el de la luna en la curva del lomo.
        if (a.oscuro && luz == 0f) {
            drawLine(a.filo((0.8f * MuebleLibretaRevolucion.cerca(Offset(r.right, r.top), s, a)).coerceAtMost(0.7f)), Offset(r.right - 0.6f * U, r.top + 2f * U), Offset(r.right - 0.6f * U, r.bottom - 2f * U), 1.2f * U)
            val curva = Path().apply {
                moveTo(r.left + 4f * U, r.top + 0.5f * U)
                cubicTo(r.left - 1f * U, r.top, r.left - 3.6f * U, r.top + alto * 0.25f, r.left - 3.6f * U, r.center.y)
                cubicTo(r.left - 3.6f * U, r.bottom - alto * 0.25f, r.left - 1f * U, r.bottom, r.left + 4f * U, r.bottom - 0.5f * U)
            }
            drawPath(curva, a.lunar(0.16f), style = Stroke(2.4f * U))
        }
        // La etiqueta de papel impresa, pegada arriba en el papel verde, con su doble filete: el nombre y la cuenta.
        val izq = r.left + lomo + 6f * U
        val ew = minOf(r.right - 8f * U - izq, 132f * U)
        val ex = izq + (r.right - 8f * U - izq - ew) / 2
        val et = Rect(ex, r.top + alto * 0.15f, ex + ew, r.top + alto * 0.15f + 48f * U)
        val ke = MuebleLibretaRevolucion.cerca(et.center, s, a)
        drawRect(a.sombra(0.35f), Offset(et.left + (if (a.oscuro) -1.2f else 1.4f) * U, et.top + 1.8f * U), et.size)
        val base = mezcla(P.PAPEL, P.TOSTADO, 0.4f)
        val papel = if (luz > 0f) a.sol(P.TOSTADO, a.dia(base, 0.04f), luz) else if (a.oscuro) a.etiqueta(base, maxOf(ke, 0.55f)) else base
        drawRect(papel, et.topLeft, et.size)
        drawRect(a.sombra(0.12f), Offset(et.left, et.bottom - 5f * U), Size(et.width, 5f * U))
        drawRect(a.claro(0.4f), et.topLeft, Size(et.width, 0.8f * U))
        val tinta = if (a.oscuro) Color(0xFF140C08) else P.TINTA
        val f1 = et.deflate(2.4f * U); val f2 = et.deflate(4.2f * U)
        drawRect(tinta.copy(alpha = 0.75f), f1.topLeft, f1.size, style = Stroke(1.2f * U))
        drawRect(tinta.copy(alpha = 0.5f), f2.topLeft, f2.size, style = Stroke(0.5f * U))
        val corte = et.top + et.height * 0.58f
        textoEn(medidor, e.nombre, Rect(et.left + 7f * U, et.top + 5f * U, et.right - 7f * U, corte), P.LETRA, tinta, 14f, peso = FontWeight.Bold, minimo = 7f)
        drawLine(tinta.copy(alpha = 0.55f), Offset(et.left + et.width * 0.3f, corte), Offset(et.right - et.width * 0.3f, corte), 0.7f * U)
        textoEn(medidor, e.cuenta, Rect(et.left + 7f * U, corte + 1f * U, et.right - 7f * U, et.bottom - 5f * U), P.LETRA, tinta.copy(alpha = 0.85f), 9.5f, minimo = 6f)
    }

    /** La cinta de seda del registro: sale del pie entre las hojas y cuelga por delante del reborde, con la punta en cola de golondrina. */
    private fun DrawScope.cinta(s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect) {
        val U = s.U
        val x = r.left + r.width * 0.7f + s.gx * 0.5f
        val y0 = r.bottom + s.gy * 0.5f
        val fin = s.faldon + 9f * U
        val k = MuebleLibretaRevolucion.cerca(Offset(x, s.reborde), s, a)
        val col = a.dia(CINTA, 0f, k)
        val cinta = Path().apply {
            moveTo(x - 1.6f * U, y0); lineTo(x + 1.6f * U, y0)
            quadraticTo(x + 2.6f * U, s.reborde + 2f * U, x + 2.4f * U, s.reborde + 4f * U)
            lineTo(x + 3.2f * U, fin); lineTo(x + 1.6f * U, fin - 2.6f * U); lineTo(x, fin)
            lineTo(x - 0.8f * U, s.reborde + 4f * U)
            quadraticTo(x - 1f * U, s.reborde + 2f * U, x - 1.6f * U, y0); close()
        }
        drawPath(Path().apply { addPath(cinta, Offset(if (a.oscuro) -1.4f * U else 1.4f * U, 1f * U)) }, a.sombra(0.35f))
        drawPath(cinta, col)
        drawLine(a.claro(0.35f), Offset(x - 0.4f * U, s.reborde + 0.5f * U), Offset(x - 0.2f * U, s.reborde + 2.6f * U), 0.8f * U, StrokeCap.Round)
    }

    /** La primera página: el papel rayado, el margen rojo y los renglones a pluma (rayas, sin letras). */
    private fun DrawScope.pagina(e: EstadoLibreta, s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect) {
        val U = s.U
        val pag = Rect(r.left + 2f * U, r.top + 2f * U, r.right - 1f * U, r.bottom - 2f * U)
        drawRect(a.dia(P.PAPEL_LUZ, 0f, MuebleLibretaRevolucion.cerca(pag.center, s, a)), pag.topLeft, pag.size)
        drawRect(a.sombra(0.14f), pag.topLeft, Size(9f * U, pag.height))
        clipRect(pag.left, pag.top, pag.right, pag.bottom) {
            val rayas = Path()
            var y = pag.top + 14f * U
            while (y < pag.bottom - 4f * U) { rayas.moveTo(pag.left + 8f * U, y); rayas.lineTo(pag.right - 4f * U, y); y += 7f * U }
            drawPath(rayas, a.dia(P.AZUL, 0f, 0.6f).copy(alpha = 0.22f), style = Stroke(0.5f * U))
            drawLine(a.dia(P.ROJO, 0f, 0.6f).copy(alpha = 0.6f), Offset(pag.left + 26f * U, pag.top), Offset(pag.left + 26f * U, pag.bottom), 0.7f * U)
            val pluma = Path()
            var ly = pag.top + 21f * U; var j = 0
            while (ly < pag.bottom - 10f * U) {
                var x = pag.left + 30f * U; var q = 0
                while (x < pag.right - 8f * U) {
                    val l = minOf((4f + 10f * azar(e.semilla + j, q)) * U, pag.right - 8f * U - x)
                    pluma.moveTo(x, ly - 1.5f * U); pluma.lineTo(x + l, ly - 1.5f * U)
                    x += l + 2.4f * U; q++
                }
                ly += 7f * U; j++
            }
            drawPath(pluma, a.dia(P.TINTA, 0f, 0.6f).copy(alpha = 0.55f), style = Stroke(1.1f * U))
        }
    }
}
