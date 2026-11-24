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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo fijo del rincón de la libreta de la Ciudad verde (se pinta una vez por tamaño y modo): el papel pintado viejo con su
 * roto, la mancha del lucernario (de día) o el charco del candil (de noche), la tubería de hierro con la
 * hiedra y la gotera, el sombrero de paja colgado, la mesa de roble con su cajón, el candil de botella y el cuenco de las pipas
 * con el lápiz y sus virutas.
 */
internal object RinconLibretaRuinas {
    /** El papel pintado: dos verdes polvorientos a rayas anchas, el filete crema y el ramito desvaído; su revés. */
    private val PAPEL_A = Color(0xFF9CAE8A)
    private val PAPEL_B = Color(0xFF889C7A)
    private val PAPEL_REVES = Color(0xFFD9D2BC)
    /** El yeso que asoma por el roto: el verde agua de la casa, más blanqueado por la cal (para que no sea un parche azul). */
    private val YESO_ROTO = mezcla(P.YESO, P.YESO_BLANCO, 0.3f)
    /** El hierro de la tubería y el ámbar del vidrio del candil. */
    private val TUBO = Color(0xFF4A4C48)
    private val AMBAR = Color(0xFF8A4E1C)
    private val PAJA = Color(0xFFD8B872)
    private val PAJA_OSC = Color(0xFF9C7E44)

    fun DrawScope.rincon(s: SitioLibretaRuinas, a: AireRuinas) {
        pared(s, a)
        tuberia(s, a)
        sombrero(s, a)
        mesa(s, a)
        candil(s, a)
        cuenco(s, a)
    }

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        papelPintado(s, a, null)
        // De día, la mancha del lucernario pinta el papel (más claro y cálido; el paño del vidrio con verdín, más verde), con
        // borde neto. De noche no hay paño de luna: el papel lejos del candil se hunde en el verde botella.
        if (!a.oscuro) for ((v, minimo, maximo) in listOf(Triple(0.62f, 0.3f, 0.9f), Triple(1f, 0.9f, 2f))) {
            val m = s.sol.camino(U, minimo, maximo)
            clipPath(m) { papelPintado(s, a, v) }
        }
        desgarro(s, a)
        humedad(s, a)
    }

    /** El papel pintado entero, con la luz que le toca: [sol] (de día) forzado para pintar su mancha. */
    private fun DrawScope.papelPintado(s: SitioLibretaRuinas, a: AireRuinas, sol: Float?) {
        val U = s.U; val w = s.w
        fun l(p: Offset): LuzRuinas = s.luz(a, p, sol ?: 0f)
        val alto = s.suelo
        if (a.oscuro) {
            // De noche: el papel hundido en el verde botella y, encima, el charco del candil que lo pinta con su color encendido.
            drawRect(l(Offset(w, 0f)).c(PAPEL_A), Offset.Zero, Size(w, alto))
            charco(s, a, PAPEL_A, 150f * U, alto)
        } else {
            val c0 = l(Offset(s.cx - 140f * U, 20f * U)).c(PAPEL_A)
            val c1 = l(Offset(s.cx + 160f * U, s.mesa)).c(PAPEL_A)
            drawRect(Brush.linearGradient(listOf(c0, c1), Offset(s.cx - 180f * U, 0f), Offset(s.cx + 180f * U, alto)), Offset.Zero, Size(w, alto))
        }
        // Las rayas anchas, un poco más hondas, y los filetes crema entre ellas.
        val paso = 26f * U
        var x = s.cx - 13f * U - paso * 20
        var i = 0
        val raya = a.sombra(if (a.oscuro) 0.16f else 0.11f)
        while (x < w) {
            if (x + paso > 0f) {
                if (i % 2 == 1) drawRect(raya, Offset(x, 0f), Size(paso, alto))
                drawRect(l(Offset(x, s.mesa * 0.5f)).c(PAPEL_REVES).copy(alpha = 0.22f), Offset(x - 0.6f * U, 0f), Size(1.2f * U, alto))
            }
            x += paso; i++
        }
        // El ramito desvaído, ralo, en las rayas claras: dos hojitas y un tallo, del crema del revés.
        var fila = 0
        var y = 18f * U
        while (y < alto) {
            var xm = s.cx - 13f * U - paso * 20 + paso * 0.5f + (if (fila % 2 == 0) 0f else paso * 2)
            while (xm < w) {
                if (xm > -paso) {
                    val c = l(Offset(xm, y)).c(PAPEL_REVES).copy(alpha = 0.32f)
                    drawLine(c, Offset(xm, y + 4f * U), Offset(xm, y - 4f * U), 0.8f * U)
                    drawOval(c, Offset(xm - 4.5f * U, y - 3f * U), Size(4f * U, 2.2f * U))
                    drawOval(c, Offset(xm + 0.5f * U, y - 1f * U), Size(4f * U, 2.2f * U))
                }
                xm += paso * 4
            }
            y += 40f * U; fila++
        }
        pinceladas(Rect(0f, 0f, w, alto), if (a.oscuro) a.hondo else P.YESO_SOMBRA, 1501, 40, U, if (a.oscuro) 0.12f else 0.08f)
    }

    /** De noche, el charco del candil sobre una superficie de color [col]: el color que tiene con la luz a cada distancia. */
    private fun DrawScope.charco(s: SitioLibretaRuinas, a: AireRuinas, col: Color, radio: Float, hasta: Float) {
        val n = 7
        val stops = Array(n + 1) { i ->
            val f = i / n.toFloat()
            val p = Offset(s.llama.x + radio * f * 0.8f, s.llama.y + radio * f * 0.6f)
            f to s.luz(a, p).c(col).copy(alpha = if (f < 0.6f) 1f else (1f - (f - 0.6f) / 0.4f).coerceIn(0f, 1f))
        }
        clipRect(0f, 0f, s.w, hasta) { drawCircle(Brush.radialGradient(colorStops = stops, center = s.llama, radius = radio), radio, s.llama) }
    }

    /**
     * El roto del papel pintado, arriba a la izquierda: de borde irregular, con el filo blanco y desparejo de la fibra rota; dentro,
     * el yeso verde agua con sus poros, una grieta y el ladrillo donde se cayó; y dos lenguas del papel despegadas que se
     * abarquillan hacia fuera (el revés claro, el rulo de la punta con la cara pintada y su sombra en el yeso). Se pinta entero con
     * la luz del cuarto y otra vez, recortado por la mancha del lucernario, con la del sol: la mancha pasa por encima del papel y
     * del yeso con el mismo borde. De noche es el mismo roto, hundido, con el charco del candil encima.
     */
    private fun DrawScope.desgarro(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val c = Offset(s.cx - 162f * U, 86f * U)
        if (c.x < -20f * U) return
        val rx = 27f * U; val ry = 31f * U
        // El borde: un óvalo torcido (dos ondas largas) con dientes chicos y desparejos, como se rompe el papel.
        val n = 44
        val roto = Path()
        for (i in 0 until n) {
            val t = i / n.toFloat() * 2f * PI.toFloat()
            val r = 1f + 0.14f * sin(2f * t + 1.3f) + 0.07f * sin(5f * t + 0.4f) + (if (i % 2 == 0) 0.06f else -0.04f) * azar(1520, i)
            val p = Offset(c.x + cos(t) * rx * r, c.y + sin(t) * ry * r)
            if (i == 0) roto.moveTo(p.x, p.y) else roto.lineTo(p.x, p.y)
        }
        roto.close()
        // Hacia dónde caen las sombras: de día, el lucernario está arriba a la izquierda; de noche, el candil, abajo a la derecha.
        val sombra = if (a.oscuro) Offset(-1f, -0.35f) else Offset(1f, 1f)
        rotoCon(s, a, c, rx, ry, roto, s.luz(a, c), sombra)
        if (!a.oscuro) for ((v, minimo, maximo) in listOf(Triple(0.62f, 0.3f, 0.9f), Triple(1f, 0.9f, 2f)))
            clipPath(s.sol.camino(U, minimo, maximo)) { rotoCon(s, a, c, rx, ry, roto, s.luz(a, c, v), sombra) }
    }

    /** El roto entero con la luz [l] y las sombras corridas hacia [sombra]: el yeso, la grieta, el ladrillo, el canto, la fibra y las lenguas. */
    private fun DrawScope.rotoCon(s: SitioLibretaRuinas, a: AireRuinas, c: Offset, rx: Float, ry: Float, roto: Path, l: LuzRuinas, sombra: Offset) {
        val U = s.U
        clipPath(roto) {
            // El yeso con su luz (de noche, el charco del candil encima), las manchas de cal y los poros.
            drawRect(l.c(YESO_ROTO), Offset(c.x - 2f * rx, c.y - 2f * ry), Size(4f * rx, 4f * ry))
            if (a.oscuro) charco(s, a, YESO_ROTO, 150f * U, s.suelo)
            pinceladas(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry), l.c(P.YESO_SOMBRA), 1521, 16, U, 0.3f)
            for (k in 0 until 16) drawCircle(l.c(P.YESO_SOMBRA).copy(alpha = 0.6f), (0.5f + 0.8f * azar(1523, k)) * U,
                Offset(c.x + (azar(1524, k) - 0.5f) * 1.8f * rx, c.y + (azar(1525, k) - 0.5f) * 1.8f * ry))
            // Abajo se cayó el yeso: el ladrillo, hundido, con la llaga de mortero y el canto del yeso que lo rodea.
            val lad = desconchonRuinas(Offset(c.x + 5f * U, c.y + 15f * U), 13f * U, 9f * U, 1522, 11)
            drawPath(lad, l.c(mezcla(P.LADRILLO, P.YESO_SOMBRA, 0.3f)))
            drawLine(l.c(P.YESO_BLANCO).copy(alpha = 0.6f), Offset(c.x - 8f * U, c.y + 16f * U), Offset(c.x + 18f * U, c.y + 16f * U), 1f * U)
            drawPath(bordeRuinas(lad, sombra.x * 1.6f * U, sombra.y * 1.6f * U), l.s(0.55f))
            // La grieta del yeso, con su labio de luz del lado contrario a la sombra.
            val grieta = Path().apply {
                moveTo(c.x - 22f * U, c.y - 14f * U); lineTo(c.x - 17f * U, c.y - 12f * U); lineTo(c.x - 13f * U, c.y - 6f * U); lineTo(c.x - 10f * U, c.y - 5f * U)
                lineTo(c.x - 8f * U, c.y + 1f * U); lineTo(c.x - 3f * U, c.y + 2f * U); lineTo(c.x + 3f * U, c.y + 6f * U)
                moveTo(c.x - 8f * U, c.y + 1f * U); lineTo(c.x - 11f * U, c.y + 6f * U); lineTo(c.x - 10f * U, c.y + 11f * U)
            }
            translate(-sombra.x * 0.7f * U, -sombra.y * 0.7f * U) { drawPath(grieta, l.b(0.45f), style = Stroke(0.6f * U)) }
            drawPath(grieta, l.s(0.42f), style = Stroke(0.75f * U, cap = StrokeCap.Round))
            // El canto del papel hace sombra en el yeso del lado de la luz.
            drawPath(bordeRuinas(roto, sombra.x * 3f * U, sombra.y * 3f * U), l.s(0.5f))
        }
        // El filo de la fibra rota, blanco y desparejo.
        drawPath(roto, l.c(PAPEL_REVES).copy(alpha = 0.85f), style = Stroke(1.3f * U))
        // Las dos lenguas despegadas: una cuelga del borde de arriba, la otra se dobla desde el de la izquierda.
        lengua(c + Offset(-11f * U, -0.97f * ry), c + Offset(9f * U, -0.95f * ry), c + Offset(2f * U, -0.4f * ry), l, sombra, U)
        lengua(c + Offset(-1.14f * rx, 1f * U), c + Offset(-1f * rx, 16f * U), c + Offset(-0.48f * rx, 12f * U), l, sombra, U)
    }

    /**
     * Una lengua del papel despegada de la pared y abarquillada hacia fuera: nace en el borde del roto entre [raiz0] y [raiz1] y
     * cae hacia [punta], afinándose en una punta rota. Se le ve el revés (claro), el doblez de la raíz más hondo y, a lo largo de
     * un costado, el canto que se enrolla hacia fuera y deja ver una tira de la cara pintada; como está separada de la pared, su
     * sombra cae corrida en el yeso.
     */
    private fun DrawScope.lengua(raiz0: Offset, raiz1: Offset, punta: Offset, l: LuzRuinas, sombra: Offset, U: Float) {
        val medio = (raiz0 + raiz1) / 2f
        val baja = punta - medio
        val ancho = raiz1 - raiz0
        val pico = punta + ancho * 0.18f
        val c1 = raiz1 + baja * 0.55f + ancho * 0.08f
        val c0 = raiz0 + baja * 0.8f + ancho * 0.1f
        val muesca = punta - ancho * 0.12f - baja * 0.12f
        val cuerpo = Path().apply {
            moveTo(raiz0.x, raiz0.y); lineTo(raiz1.x, raiz1.y)
            quadraticTo(c1.x, c1.y, pico.x, pico.y)
            lineTo(muesca.x, muesca.y)
            quadraticTo(c0.x, c0.y, raiz0.x, raiz0.y)
            close()
        }
        translate(sombra.x * 3.5f * U, sombra.y * 4f * U) { drawPath(cuerpo, l.s(0.45f)) }
        drawPath(cuerpo, l.c(PAPEL_REVES))
        // El doblez de la raíz, donde el papel se despega de la pared.
        drawLine(l.s(0.28f), raiz0 + baja * 0.1f, raiz1 + baja * 0.1f, 2.2f * U)
        // El canto que se enrolla hacia fuera, a lo largo del costado: una tira de la cara pintada, con su sombrita por dentro.
        val canto = Path().apply { val p = raiz1 + baja * 0.12f; moveTo(p.x, p.y); quadraticTo(c1.x, c1.y, pico.x, pico.y) }
        val dentro = ancho * (-1.6f * U / ancho.getDistance().coerceAtLeast(1f))
        translate(dentro.x, dentro.y) { drawPath(canto, l.s(0.3f), style = Stroke(0.9f * U)) }
        drawPath(canto, l.c(PAPEL_A), style = Stroke(2.2f * U, cap = StrokeCap.Round))
    }

    /** Las manchas de humedad que bajan de la junta de la tubería, con su cerco. */
    private fun DrawScope.humedad(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val x = s.cx + 64f * U
        val l = s.luz(a, Offset(x, 90f * U))
        val mojado = Path().apply {
            moveTo(x - 10f * U, s.tuberia + 4f * U); lineTo(x + 12f * U, s.tuberia + 4f * U)
            cubicTo(x + 22f * U, 80f * U, x + 6f * U, 120f * U, x + 18f * U, 150f * U)
            lineTo(x - 20f * U, 150f * U); cubicTo(x - 10f * U, 110f * U, x - 22f * U, 70f * U, x - 10f * U, s.tuberia + 4f * U); close()
        }
        drawPath(mojado, l.c(mezcla(PAPEL_B, P.HIEDRA_OSC, 0.45f)).copy(alpha = 0.3f))
        drawPath(mojado, l.c(P.KRAFT_OSC).copy(alpha = 0.3f), style = Stroke(1f * U))
    }

    // ---------------------------------------------------------------- la tubería

    /** La tubería de hierro que cruza la pared: el tubo con su luz arriba, sus abrazaderas, la junta que gotea y la hiedra enroscada. */
    private fun DrawScope.tuberia(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U; val w = s.w
        val y = s.tuberia; val r = 6.5f * U
        val l = s.luz(a, Offset(s.cx, y))
        // La sombra en la pared: de día, abajo y a la derecha; de noche, arriba (el candil está abajo).
        val dy = if (a.oscuro) -5f * U else 7f * U
        drawRect(l.s(0.32f), Offset(0f, y - r + dy), Size(w, r * 2))
        val c = l.c(TUBO)
        drawRect(Brush.verticalGradient(listOf(mezcla(c, Color.White, 0.3f), c, mezcla(c, a.hondo, 0.5f)), y - r, y + r), Offset(0f, y - r), Size(w, r * 2))
        drawLine(l.b(0.5f), Offset(0f, y - r * 0.55f), Offset(w, y - r * 0.55f), 1.2f * U)
        // El óxido en placas de borde neto, en la mitad de abajo (donde se junta el agua), no en halos.
        clipRect(0f, y - r, w, y + r) {
            for (i in 0 until 8) {
                val lw = (8f + 14f * azar(1531, i)) * U
                drawOval(l.c(P.BARRO).copy(alpha = 0.4f), Offset(w * azar(1530, i) - lw / 2f, y - r * 0.05f), Size(lw, r * 1.3f))
            }
        }
        // Las abrazaderas clavadas a la pared.
        for (x in listOf(s.cx - 150f * U, s.cx + 118f * U)) {
            drawRect(l.s(0.35f), Offset(x - 3f * U + 2f * U, y - r - 3f * U + 2f * U), Size(6f * U, r * 2 + 6f * U))
            drawRect(l.c(mezcla(TUBO, P.HIERRO, 0.4f)), Offset(x - 3f * U, y - r - 3f * U), Size(6f * U, r * 2 + 6f * U))
            drawLine(l.b(0.4f), Offset(x - 3f * U, y - r - 3f * U), Offset(x - 3f * U, y + r + 3f * U), 1f * U)
            for (dyy in listOf(-r - 5f * U, r + 5f * U)) drawCircle(l.c(P.HIERRO), 1.6f * U, Offset(x, y + dyy))
        }
        // La junta: dos tuercas de seis caras, con su óxido, y la gota que cuelga.
        val jx = s.cx + 64f * U
        for (dx in listOf(-5f * U, 5f * U)) {
            val t = Rect(jx + dx - 4f * U, y - r - 2f * U, jx + dx + 4f * U, y + r + 2f * U)
            drawRect(l.c(mezcla(TUBO, P.HIERRO_LUZ, 0.3f)), t.topLeft, t.size)
            drawLine(l.b(0.4f), Offset(t.left + 1f * U, t.top), Offset(t.left + 1f * U, t.bottom), 1f * U)
            drawLine(l.s(0.4f), Offset(t.right - 1f * U, t.top), Offset(t.right - 1f * U, t.bottom), 1f * U)
        }
        drawRect(l.c(P.BARRO).copy(alpha = 0.6f), Offset(jx - 3f * U, y + r), Size(6f * U, 16f * U))
        val gota = Offset(jx + 1f * U, y + r + 22f * U)
        val lg = s.luz(a, gota)
        drawPath(cerradaRuinas(listOf(gota + Offset(0f, -4f * U), gota + Offset(2f * U, 1f * U), gota + Offset(0f, 3f * U), gota + Offset(-2f * U, 1f * U))), lg.c(P.VIDRIO_CLARO).copy(alpha = 0.75f))
        drawCircle(if (a.oscuro) P.LUNA.copy(alpha = 0.7f) else lg.b(0.9f), 0.9f * U, gota + Offset(-0.6f * U, 0f))
        // La hiedra enroscada en el tubo, de la izquierda hasta el medio, y dos guías que cuelgan.
        val hacia = if (a.oscuro) 1f else -1f
        val pts = List(12) { i -> Offset(s.cx - 230f * U + i * 20f * U, y + (if (i % 2 == 0) -r * 0.9f else r * 0.9f)) }
        talloRuinas(pts, 13f * U, 1540, { p -> s.luz(a, p) }, hacia, 1, 1.4f)
        talloRuinas(listOf(Offset(s.cx - 120f * U, y + r), Offset(s.cx - 116f * U, y + 30f * U), Offset(s.cx - 122f * U, y + 56f * U), Offset(s.cx - 114f * U, y + 78f * U)), 12f * U, 1541, { p -> s.luz(a, p) }, hacia, 1, 1.2f)
        talloRuinas(listOf(Offset(s.cx - 38f * U, y + r), Offset(s.cx - 42f * U, y + 22f * U), Offset(s.cx - 36f * U, y + 40f * U)), 11f * U, 1542, { p -> s.luz(a, p) }, hacia, 1, 1.1f)
    }

    // ---------------------------------------------------------------- el sombrero de paja

    /** El sombrero de paja de recolectora colgado de un clavo por su cinta: el ala ancha, la copa y la cinta de tela gastada. */
    private fun DrawScope.sombrero(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val c = s.clavo
        if (c.x - 60f * U > s.w) return
        val l = s.luz(a, c + Offset(0f, 30f * U))
        val hacia = s.hacia(a, c)
        rotate(-9f, c) {
            val ala = Rect(c.x - 50f * U, c.y + 26f * U, c.x + 50f * U, c.y + 52f * U)
            val copa = cerradaRuinas(listOf(Offset(c.x - 28f * U, ala.center.y), Offset(c.x - 26f * U, c.y + 14f * U), Offset(c.x - 12f * U, c.y + 4f * U),
                Offset(c.x + 12f * U, c.y + 4f * U), Offset(c.x + 26f * U, c.y + 14f * U), Offset(c.x + 28f * U, ala.center.y)))
            // La sombra en el papel.
            translate(-hacia * 7f * U, 6f * U) { drawOval(l.s(0.3f), ala.topLeft, ala.size); drawPath(copa, l.s(0.3f)) }
            drawOval(l.c(PAJA_OSC), ala.topLeft, ala.size)
            drawOval(l.c(PAJA), Offset(ala.left + 3f * U, ala.top + 1f * U), Size(ala.width - 6f * U, ala.height * 0.62f))
            modeladoRuinas(copa, l.c(PAJA), l, hacia, 0.8f, 0.4f)
            // El trenzado de la paja: arcos a lo ancho en la copa y en el ala.
            clipPath(copa) { for (k in 0 until 5) drawArc(l.c(PAJA_OSC).copy(alpha = 0.5f), 200f, 140f, false, Offset(c.x - 30f * U, c.y + 4f * U + k * 6f * U), Size(60f * U, 14f * U), style = Stroke(0.8f * U)) }
            for (k in 0 until 3) drawOval(l.c(PAJA_OSC).copy(alpha = 0.4f), Offset(ala.left + 6f * U * (k + 1), ala.top + 2f * U * (k + 1)), Size(ala.width - 12f * U * (k + 1), ala.height - 4f * U * (k + 1)), style = Stroke(0.7f * U))
            // La cinta de tela gastada en la base de la copa, y el lazo que cuelga del clavo.
            drawRect(l.c(mezcla(P.LADRILLO, P.GASTADO, 0.35f)), Offset(c.x - 27f * U, ala.center.y - 9f * U), Size(54f * U, 6f * U))
            drawLine(l.c(mezcla(P.LADRILLO, P.GASTADO, 0.35f)), Offset(c.x - 4f * U, c.y + 5f * U), c, 1.4f * U)
            // De noche, el filo frío de la luna en los cantos de la derecha de la copa y del ala.
            if (a.oscuro) {
                drawPath(bordeRuinas(copa, -2f * U, 1.4f * U), s.frio(a, c + Offset(26f * U, 14f * U), 0.6f))
                drawPath(bordeRuinas(Path().apply { addOval(ala) }, -2.4f * U, 0f), s.frio(a, Offset(ala.right, ala.center.y), 0.6f))
            }
        }
        with(ParedEstanteRuinas) { clavo(c, s.luz(a, c)) }
    }

    // ---------------------------------------------------------------- la mesa

    /** La mesa de roble de la sala: la cara de arriba vista un poco desde lo alto, su canto, el faldón con el cajón y las patas. */
    private fun DrawScope.mesa(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U; val w = s.w; val h = s.h
        val izq = s.cx - 178f * U; val der = s.cx + 178f * U
        // Debajo de la mesa: la pared en sombra y el suelo de baldosa, hondos.
        val hondo = if (a.oscuro) a.hondo else Color(0xFF22160E)
        drawRect(mezcla(s.luz(a, Offset(s.cx, s.patas)).c(PAPEL_B), hondo, 0.6f), Offset(0f, s.faldon), Size(w, s.suelo - s.faldon))
        drawRect(mezcla(s.luz(a, Offset(s.cx, h)).c(mezcla(P.OCRE, P.BALDOSA_GRIS, 0.42f)), hondo, 0.45f), Offset(0f, s.suelo), Size(w, h - s.suelo))
        var x = s.cx - 30f * U * 8
        while (x < w) { drawLine(hondo.copy(alpha = 0.5f), Offset(x, s.suelo), Offset(x + (x - s.cx) * 0.3f, h), 1f * U); x += 30f * U }
        // Las patas, torneadas en tres anillos.
        for (px in listOf(izq + 10f * U, der - 10f * U)) {
            val lp = s.luz(a, Offset(px, s.patas + 10f * U))
            drawRect(Brush.horizontalGradient(listOf(lp.c(P.ROBLE_LUZ), lp.c(P.ROBLE), mezcla(lp.c(P.ROBLE_OSC), hondo, 0.4f)), px - 8f * U, px + 8f * U), Offset(px - 8f * U, s.faldon), Size(16f * U, h - s.faldon))
            for (k in 0 until 3) drawOval(lp.s(0.35f), Offset(px - 9f * U, s.patas + k * 8f * U), Size(18f * U, 3f * U))
        }
        // La cara de arriba (de la pared al canto): un roble más claro con su veta a lo largo y los cercos de los vasos.
        val arriba = s.mesa - 12f * U
        val xs = List(6) { izq + (der - izq) * it / 5f }
        drawRect(Brush.horizontalGradient(xs.map { s.luz(a, Offset(it, s.mesa), s.solEn(Offset(it, s.mesa), 6f)).c(P.ROBLE_LUZ) }, izq, der), Offset(izq, arriba), Size(der - izq, s.canto - arriba))
        vetasMesa(s, a, Rect(izq, arriba, der, s.canto))
        for (i in 0 until 3) {
            val c = Offset(s.cx + (azar(1550, i) - 0.5f) * 300f * U, s.mesa + 1f * U)
            drawOval(s.luz(a, c).c(P.ROBLE_OSC).copy(alpha = 0.3f), Offset(c.x - 12f * U, c.y - 3f * U), Size(24f * U, 6f * U), style = Stroke(1f * U))
        }
        drawRect(a.sombra(0.3f), Offset(izq, arriba), Size(der - izq, 3f * U))
        // El canto, con su luz arriba y la sombra debajo; el faldón con el cajón y su tirador.
        drawRect(Brush.horizontalGradient(xs.map { s.luz(a, Offset(it, s.canto)).c(P.ROBLE) }, izq, der), Offset(izq, s.canto), Size(der - izq, s.faldon - s.canto))
        drawLine(Brush.horizontalGradient(xs.map { s.luz(a, Offset(it, s.canto)).b(0.55f) }, izq, der), Offset(izq, s.canto + 0.8f * U), Offset(der, s.canto + 0.8f * U), 1.2f * U)
        drawRect(Brush.horizontalGradient(xs.map { mezcla(s.luz(a, Offset(it, s.patas)).c(P.ROBLE_OSC), hondo, 0.15f) }, izq, der), Offset(izq, s.faldon), Size(der - izq, s.patas - s.faldon))
        drawRect(a.sombra(0.4f), Offset(izq, s.faldon), Size(der - izq, 3f * U))
        val cajon = Rect(s.cx - 60f * U, s.faldon + 5f * U, s.cx + 60f * U, s.patas - 4f * U)
        val lc = s.luz(a, cajon.center)
        drawRect(lc.c(P.ROBLE), cajon.topLeft, cajon.size)
        drawRect(lc.s(0.45f), cajon.topLeft, Size(cajon.width, 1.6f * U))
        drawRect(lc.b(0.3f), Offset(cajon.left, cajon.bottom - 1.2f * U), Size(cajon.width, 1.2f * U))
        drawCircle(lc.s(0.4f), 3.4f * U, cajon.center + Offset(1f * U, 1.4f * U))
        drawCircle(lc.c(P.LATON), 3f * U, cajon.center)
        drawCircle(lc.b(0.7f), 1f * U, cajon.center - Offset(1f * U, 1f * U))
        // El desgaste de tirar del cajón, alrededor del tirador: la madera más clara, de borde neto.
        drawOval(lc.c(P.GASTADO).copy(alpha = 0.3f), Offset(cajon.center.x - 9f * U, cajon.center.y - 4.5f * U), Size(18f * U, 9f * U))
        drawCircle(lc.c(P.LATON), 3f * U, cajon.center)
        drawCircle(lc.b(0.7f), 1f * U, cajon.center - Offset(1f * U, 1f * U))
        if (a.oscuro) {
            val lk = s.luz(a, Offset(s.llama.x, s.canto))
            if (lk.k > 0.05f) drawLine(lk.filo(0.85f), Offset(izq, s.canto + 1f * U), Offset(minOf(der, s.llama.x + 150f * U), s.canto + 1f * U), 1.4f * U)
        }
    }

    /** La veta de la cara de la mesa, a lo largo, en un solo camino. */
    private fun DrawScope.vetasMesa(s: SitioLibretaRuinas, a: AireRuinas, r: Rect) {
        val U = s.U
        val p = Path()
        for (k in 0 until 5) {
            val y0 = r.top + r.height * (0.15f + 0.17f * k)
            p.moveTo(r.left, y0)
            var x = r.left; var j = 0
            while (x < r.right) { x += 30f * U; p.lineTo(minOf(x, r.right), y0 + (azar(1560 + k, j) - 0.5f) * 2f * U); j++ }
        }
        drawPath(p, s.luz(a, r.center).c(P.ROBLE_OSC).copy(alpha = 0.35f), style = Stroke(0.7f * U))
    }

    // ---------------------------------------------------------------- el candil de botella

    /**
     * El candil hecho con una botella de vidrio ámbar: el aceite dentro, el gollete con su tapón de hojalata y el tubito de la
     * mecha, sobre un platillo. De noche arde: la llama, el halo y el vidrio encendido.
     */
    private fun DrawScope.candil(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val c = s.candil
        if (c.x + 30f * U < 0f) return
        val l = s.luz(a, Offset(c.x, c.y - 30f * U), s.solEn(Offset(c.x, c.y - 30f * U), 10f))
        val arde = a.oscuro
        val an = 26f * U
        val forma = cerradaRuinas(listOf(Offset(c.x - 5f * U, c.y - 70f * U), Offset(c.x + 5f * U, c.y - 70f * U), Offset(c.x + 6f * U, c.y - 54f * U),
            Offset(c.x + an / 2, c.y - 40f * U), Offset(c.x + an / 2, c.y - 4f * U), Offset(c.x - an / 2, c.y - 4f * U), Offset(c.x - an / 2, c.y - 40f * U), Offset(c.x - 6f * U, c.y - 54f * U)))
        if (arde) {
            mancha(s.llama, 70f * U, P.LLAMA.copy(alpha = 0.32f))
        } else drawPath(corridoRuinas(forma, 9f * U, 4f * U), l.s(0.3f))
        // El platillo de esmalte.
        drawOval(l.s(0.45f), Offset(c.x - 22f * U, c.y - 3f * U), Size(44f * U, 6f * U))
        drawOval(l.c(P.ESMALTE), Offset(c.x - 21f * U, c.y - 5f * U), Size(42f * U, 7f * U))
        drawOval(l.c(P.ESMALTE_BLANCO), Offset(c.x - 17f * U, c.y - 5f * U), Size(34f * U, 4f * U))
        // La botella: el vidrio ámbar, el aceite dentro (más hondo), los reflejos.
        val vidrio = if (arde) mezcla(l.c(AMBAR), P.FUEGO, 0.35f) else l.c(AMBAR)
        drawPath(forma, vidrio.copy(alpha = 0.92f))
        clipPath(forma) {
            drawRect(mezcla(vidrio, Color(0xFF3A1E08), 0.4f), Offset(c.x - an, c.y - 30f * U), Size(an * 2, 30f * U))
            drawLine(l.b(0.4f), Offset(c.x - an / 2, c.y - 30f * U), Offset(c.x + an / 2, c.y - 30f * U), 0.8f * U)
            if (arde) drawRect(P.LLAMA.copy(alpha = 0.25f), Offset(c.x - an, c.y - 74f * U), Size(an * 2, 74f * U))
        }
        drawRoundRect(if (arde) P.LLAMA.copy(alpha = 0.7f) else l.b(0.8f), Offset(c.x - an / 2 + 3f * U, c.y - 38f * U), Size(3f * U, 26f * U), CornerRadius(1.5f * U))
        drawPath(forma, mezcla(vidrio, a.hondo, 0.4f), style = Stroke(0.9f * U))
        // El tapón de hojalata con el tubito y la mecha.
        drawRect(l.c(P.NIQUEL), Offset(c.x - 6f * U, c.y - 74f * U), Size(12f * U, 5f * U))
        drawRect(l.c(P.HIERRO_LUZ), Offset(c.x - 1.5f * U, c.y - 80f * U), Size(3f * U, 6f * U))
        drawLine(l.c(Color(0xFF2A2420)), Offset(c.x, c.y - 80f * U), Offset(c.x + 0.6f * U, c.y - 83f * U), 1.4f * U)
        if (arde) {
            val f = s.llama
            mancha(f, 22f * U, P.LLAMA.copy(alpha = 0.7f))
            drawPath(cerradaRuinas(listOf(f + Offset(0f, -13f * U), f + Offset(4.5f * U, 0f), f + Offset(0f, 4f * U), f + Offset(-4.5f * U, 0f))), P.FUEGO)
            drawPath(cerradaRuinas(listOf(f + Offset(0f, -7f * U), f + Offset(2.4f * U, 0.5f * U), f + Offset(0f, 3f * U), f + Offset(-2.4f * U, 0.5f * U))), Color(0xFFFFF4C8))
        }
    }

    // ---------------------------------------------------------------- el cuenco de las pipas y el lápiz

    /** El cuenco de barro colmado de pipas de girasol que se secan, unas sueltas en la mesa, y el lápiz con sus virutas. */
    private fun DrawScope.cuenco(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val c = s.cuenco
        val l = s.luz(a, Offset(c.x, c.y - 12f * U))
        val hacia = s.hacia(a, c)
        if (c.x - 40f * U < s.w) {
            val an = 52f * U; val al = 22f * U
            contactoRuinas(c.x, c.y, an * 1.05f, 5f * U, l, hacia, 0.5f)
            // El montón de pipas por encima del borde.
            val monton = cerradaRuinas(listOf(Offset(c.x - an * 0.46f, c.y - al), Offset(c.x - an * 0.2f, c.y - al - 10f * U), Offset(c.x + an * 0.1f, c.y - al - 13f * U), Offset(c.x + an * 0.4f, c.y - al - 6f * U), Offset(c.x + an * 0.46f, c.y - al)))
            drawPath(monton, l.c(Color(0xFF3E3A34)))
            for (k in 0 until 14) {
                val p = Offset(c.x + an * (azar(1570, k) - 0.5f) * 0.8f, c.y - al - 9f * U * azar(1571, k))
                rotate(azar(1572, k) * 180f, p) {
                    drawOval(l.c(Color(0xFF2E2A26)), Offset(p.x - 2.6f * U, p.y - 1.4f * U), Size(5.2f * U, 2.8f * U))
                    drawLine(l.c(Color(0xFFD9D2BC)).copy(alpha = 0.8f), Offset(p.x - 2f * U, p.y), Offset(p.x + 2f * U, p.y), 0.6f * U)
                }
            }
            val cuerpo = cerradaRuinas(listOf(Offset(c.x - an / 2, c.y - al), Offset(c.x + an / 2, c.y - al), Offset(c.x + an * 0.4f, c.y - al * 0.3f), Offset(c.x + an * 0.2f, c.y - 1f * U),
                Offset(c.x - an * 0.2f, c.y - 1f * U), Offset(c.x - an * 0.4f, c.y - al * 0.3f)))
            modeladoRuinas(cuerpo, l.c(P.BARRO), l, hacia, 1f, 0.35f)
            drawOval(l.c(mezcla(P.BARRO, P.OCRE, 0.3f)), Offset(c.x - an / 2, c.y - al - 2.5f * U), Size(an, 5f * U), style = Stroke(1.6f * U))
            // De noche, el filo frío de la luna en el costado derecho del cuenco y en el montón.
            if (a.oscuro) {
                drawPath(bordeRuinas(cuerpo, -2f * U, 0f), s.frio(a, Offset(c.x + an / 2, c.y - al), 0.7f))
                drawPath(bordeRuinas(monton, -1.6f * U, 1.2f * U), s.frio(a, Offset(c.x + an * 0.3f, c.y - al - 8f * U), 0.45f))
            }
            for (k in 0 until 4) {
                val p = Offset(c.x - an * 0.7f + 10f * U * k, c.y + 3f * U + 2f * U * (k % 2))
                drawOval(l.c(Color(0xFF2E2A26)), Offset(p.x - 2.6f * U, p.y - 1.4f * U), Size(5.2f * U, 2.8f * U))
            }
        }
        // El lápiz tumbado en la mesa, con la punta sacada a navaja, y sus virutas rizadas.
        val lp = Offset(s.tapa.right + s.v.x + 10f * U, s.mesa + 3f * U)
        val ll = s.luz(a, lp)
        rotate(-8f, lp) {
            drawRect(ll.s(0.4f), Offset(lp.x + 1f * U, lp.y + 1.6f * U), Size(48f * U, 4.4f * U))
            drawRect(ll.c(Color(0xFF3F6A3A)), Offset(lp.x, lp.y), Size(40f * U, 4.4f * U))
            drawLine(ll.b(0.4f), Offset(lp.x, lp.y + 0.8f * U), Offset(lp.x + 40f * U, lp.y + 0.8f * U), 0.8f * U)
            drawPath(poligonoRuinas(Offset(lp.x + 40f * U, lp.y), Offset(lp.x + 48f * U, lp.y + 2.2f * U), Offset(lp.x + 40f * U, lp.y + 4.4f * U)), ll.c(Color(0xFFD9B884)))
            drawLine(ll.c(P.LAPIZ), Offset(lp.x + 45f * U, lp.y + 2.2f * U), Offset(lp.x + 48f * U, lp.y + 2.2f * U), 1.2f * U)
        }
        for (k in 0 until 3) {
            val v = Offset(lp.x + 54f * U + 7f * U * k, lp.y + 4f * U)
            drawArc(ll.c(Color(0xFFD9B884)), 180f, 250f, false, Offset(v.x - 3f * U, v.y - 3f * U), Size(6f * U, 5f * U), style = Stroke(1.6f * U))
            drawArc(ll.c(Color(0xFF3F6A3A)), 180f, 60f, false, Offset(v.x - 3f * U, v.y - 3f * U), Size(6f * U, 5f * U), style = Stroke(1.6f * U))
        }
    }
}
