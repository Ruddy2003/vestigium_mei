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
 * El registro del regente ([MuebleLibretaRevolucion]), en vivo: de pie en el listón, recostado en el tablero. Media
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
        val sy = if (a.oscuro) -3f * U else 5f * U
        clipRect(s.izq, s.galeriaAbajo, s.der, s.reborde) {
            drawRect(a.sombra(0.12f), Offset(r.left + sx - 3f * U, r.top + sy - 3f * U), Size(r.width + gx + 6f * U, s.reborde - r.top))
            drawRect(a.sombra(0.22f), Offset(r.left + sx, r.top + sy), Size(r.width + gx, s.reborde - r.top))
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

    /**
     * El grueso del registro: el corte delantero a la derecha y el pie abajo. Las tapas vuelan sobre el bloque: las hojas quedan
     * metidas, con la sombra del canto de la tapa encima, y el canto de las dos tapas asoma por delante y por detrás (papel verde,
     * con el becerro en las puntas y en el lomo).
     */
    private fun DrawScope.grueso(s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect) {
        val U = s.U
        val gx = s.gx; val gy = s.gy
        val kd = MuebleLibretaRevolucion.cerca(Offset(r.right + gx, r.bottom - r.height * 0.3f), s, a)
        val kp = MuebleLibretaRevolucion.cerca(Offset(r.center.x + r.width * 0.25f, r.bottom), s, a)
        fun en(x: Float, y: Float, t: Float) = Offset(x + gx * t, y + gy * t)
        fun cara(p0: Offset, p1: Offset, p2: Offset, p3: Offset, p4: Offset = p3) =
            Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close() }
        // La tapa de atrás, entera detrás: su canto asoma al fondo de las dos caras.
        drawPath(cara(Offset(r.right, r.top), en(r.right, r.top, 1f), en(r.right, r.bottom, 1f), en(r.left, r.bottom, 1f), Offset(r.left, r.bottom)),
            a.dia(mezcla(CARTON, BECERRO_OSC, 0.45f), -0.12f, (kd + kp) / 2))
        // Las hojas, metidas entre las tapas (el corte salpicado de ocre rojo): la cara de la derecha mira lejos de la ventana y,
        // de noche, a la vela; la de abajo, de frente.
        val tb = (2.6f * U / s.g).coerceAtMost(0.3f)
        val t0 = tb; val t1 = 1f - tb
        val mete = 2.4f * U
        val hojasDer = cara(en(r.right - mete, r.top + 3f * U, t0), en(r.right - mete, r.top + 3f * U, t1), en(r.right - mete, r.bottom - 1f * U, t1), en(r.right - mete, r.bottom - 1f * U, t0))
        val hojasPie = cara(en(r.left + 5f * U, r.bottom - mete * 0.5f, t0), en(r.right - mete, r.bottom - mete * 0.5f, t0), en(r.right - mete, r.bottom - mete * 0.5f, t1), en(r.left + 5f * U, r.bottom - mete * 0.5f, t1))
        val corte = mezcla(P.PAPEL, SALPICADO, 0.2f)
        drawPath(hojasDer, a.dia(corte, if (a.oscuro) 0.05f else -0.14f, kd))
        drawPath(hojasPie, a.dia(corte, -0.06f, kp))
        // Las capas de las hojas y el salpicado.
        val capas = Path()
        val n = (s.g / (1.7f * U)).toInt().coerceIn(3, 15)
        for (i in 1 until n) {
            val t = t0 + (t1 - t0) * i / n
            val a0 = en(r.right - mete, r.top + 3f * U, t); val a1 = en(r.right - mete, r.bottom - 1f * U, t)
            capas.moveTo(a0.x, a0.y); capas.lineTo(a1.x, a1.y)
            val b0 = en(r.left + 5f * U, r.bottom - mete * 0.5f, t); val b1 = en(r.right - mete, r.bottom - mete * 0.5f, t)
            capas.moveTo(b0.x, b0.y); capas.lineTo(b1.x, b1.y)
        }
        drawPath(capas, a.sombra(0.15f), style = Stroke(0.5f * U))
        val motas = Path()
        for (j in 0 until 30) {
            val t = t0 + (t1 - t0) * azar(9700, j)
            val p = if (j % 2 == 0) en(r.right - mete, r.top + 4f * U + (r.height - 6f * U) * azar(9701, j), t)
            else en(r.left + 7f * U + (r.width - 10f * U) * azar(9701, j), r.bottom - mete * 0.5f, t)
            motas.addOval(Rect(p.x - 0.5f * U, p.y - 0.5f * U, p.x + 0.5f * U, p.y + 0.5f * U))
        }
        drawPath(motas, a.dia(SALPICADO, -0.2f, (kd + kp) / 2).copy(alpha = 0.7f))
        // La sombra que echa el canto de la tapa de delante sobre las hojas (vuela sobre ellas).
        drawPath(cara(Offset(r.right - mete, r.top + 3f * U), en(r.right - mete, r.top + 3f * U, t0 + 0.12f), en(r.right - mete, r.bottom - 1f * U, t0 + 0.12f), Offset(r.right - mete, r.bottom - 1f * U)), a.sombra(0.3f))
        if (a.oscuro) {
            // El corte brilla a la vela: el filo de las hojas que la miran.
            val f0 = en(r.right - mete, r.top + 3f * U, t1); val f1 = en(r.right - mete, r.bottom - 1f * U, t1)
            drawLine(a.filo((0.9f * kd).coerceAtMost(0.75f)), f0, f1, 1.4f * U)
        }
        // El canto de la tapa de delante: papel verde en medio, becerro en las puntas.
        val p = 30f * U
        drawPath(cara(Offset(r.right, r.top), en(r.right, r.top, t0), en(r.right, r.bottom, t0), Offset(r.right, r.bottom)), a.dia(CARTON, -0.18f, kd))
        drawPath(cara(Offset(r.right, r.top), en(r.right, r.top, t0), en(r.right, r.top + p, t0), Offset(r.right, r.top + p)), a.dia(BECERRO, -0.1f, kd))
        drawPath(cara(Offset(r.right, r.bottom - p), en(r.right, r.bottom - p, t0), en(r.right, r.bottom, t0), Offset(r.right, r.bottom)), a.dia(BECERRO, -0.1f, kd))
        drawPath(cara(Offset(r.left, r.bottom), Offset(r.right, r.bottom), en(r.right, r.bottom, t0), en(r.left, r.bottom, t0)), a.dia(CARTON, -0.25f, kp))
        drawPath(cara(Offset(r.right - p, r.bottom), Offset(r.right, r.bottom), en(r.right, r.bottom, t0), en(r.right - p, r.bottom, t0)), a.dia(BECERRO, -0.2f, kp))
        // El lomo redondo al pie: la cofia de becerro que da la vuelta de la tapa de delante a la de atrás.
        val cofia = Path().apply {
            moveTo(r.left + 2f * U, r.bottom); lineTo(r.left + 17f * U, r.bottom)
            lineTo(r.left + 17f * U + gx, r.bottom + gy); lineTo(r.left + 2f * U + gx, r.bottom + gy)
            quadraticTo(r.left - 1f * U + gx * 0.4f, r.bottom + gy * 0.7f, r.left + 2f * U, r.bottom); close()
        }
        drawPath(cofia, a.dia(BECERRO, -0.15f, kp))
        drawPath(cofia, a.sombra(0.3f), style = Stroke(0.6f * U))
        // La sombra del registro en el listón, donde se apoya.
        drawRect(a.sombra(0.45f), Offset(r.left - 3f * U, r.bottom + gy), Size(r.width + gx + 5f * U, 2f * U))
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
            drawRect(a.claro(0.36f), Offset(r.left - 2.6f * U, r.top), Size(3.4f * U, alto))
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
            // De noche, la luna fría en el filo de cada nervio (el lomo se hunde, los nervios se ven).
            drawPath(luces, if (a.oscuro) a.lunar(0.6f) else a.claro(0.45f)); drawPath(sombras, a.sombra(0.35f))
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

    /** La cinta de seda del registro: sale del pie entre las hojas y cuelga por delante del listón y del faldón, con la punta en cola de golondrina. */
    private fun DrawScope.cinta(s: SitioLibretaRevolucion, a: AireRevolucion, r: Rect) {
        val U = s.U
        val x = r.left + r.width * 0.7f + s.gx * 0.5f
        val y0 = r.bottom + s.gy * 0.5f
        val fin = s.faldonAbajo - 2f * U
        val k = MuebleLibretaRevolucion.cerca(Offset(x, s.liston), s, a)
        val col = a.dia(CINTA, 0f, k)
        val cinta = Path().apply {
            moveTo(x - 2.2f * U, y0); lineTo(x + 2.2f * U, y0)
            quadraticTo(x + 3.4f * U, s.liston, x + 3.2f * U, s.liston + 3f * U)
            lineTo(x + 4.4f * U, fin); lineTo(x + 2.2f * U, fin - 3.4f * U); lineTo(x, fin)
            lineTo(x - 1f * U, s.liston + 3f * U)
            quadraticTo(x - 1.4f * U, s.liston, x - 2.2f * U, y0); close()
        }
        drawPath(Path().apply { addPath(cinta, Offset(if (a.oscuro) -1.4f * U else 1.4f * U, 1f * U)) }, a.sombra(0.35f))
        drawPath(cinta, col)
        drawLine(if (a.oscuro) a.filo(0.5f * k) else a.claro(0.4f), Offset(x + 2.6f * U, s.liston + 0.5f * U), Offset(x + 3.6f * U, fin - 5f * U), 0.8f * U, StrokeCap.Round)
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
