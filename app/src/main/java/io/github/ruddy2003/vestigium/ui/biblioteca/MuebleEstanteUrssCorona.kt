package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de encima de la estantería (en la corona guardada), de atrás adelante: la regla en T apoyada en la pared en diagonal,
 * el proyector de cine de 16 mm del club (el cuerpo de chapa gris verdosa, la torre de la lámpara con sus rejillas, el objetivo, y
 * las dos bobinas de aluminio en sus brazos, una llena de película y la otra casi vacía, con la película entre ellas), las latas de
 * película apiladas, el vaso de té en su podstakánnik (el del Ideario, aquí bajo la lámpara colgada) y la pila de libros de la
 * biblioteca recién devueltos.
 */
internal object CoronaEstanteUrss {
    /** Lo que va detrás de la cornisa: la sombra de la tapa en la pared. */
    fun DrawScope.detras(g: MarcoUrss) {
        val w = g.w
        val y = w * 0.468f
        drawRect(g.a.sombra(if (g.a.oscuro) 0.4f else 0.22f), Offset(0f, y - w * 0.008f), Size(w, w * 0.008f))
    }

    fun DrawScope.delante(g: MarcoUrss) {
        val w = g.w
        conPanosUrss(g, 0.1f, Rect(0f, 0f, w, g.apoyo)) { s ->
            g.forzar = s
            charco(g)
            regla(g)
            // El proyector, grande: se dibuja a su medida y se agranda desde su pie de la izquierda.
            scale(1.14f, 1.14f, Offset(w * 0.06f, w * 0.49f)) {
                bobina(g, Offset(w * 0.355f, w * 0.285f), w * 0.07f, false, 8030)
                proyector(g)
                bobina(g, Offset(w * 0.165f, w * 0.258f), w * 0.095f, true, 8020)
            }
            latas(g)
            vaso(g, Offset(w * 0.627f, w * 0.492f))
            libros(g)
            g.forzar = null
        }
    }

    /**
     * Las sombras largas que echan sobre los paños de la ventana (el último sol, la luna) las cosas de delante de la pared: el
     * altavoz y el reloj, cerca de ella; las bobinas y el proyector, más lejos; la lámpara colgada, la más lejos. Solo dentro de los
     * paños: fuera, la pared ya está en la penumbra del anochecer.
     */
    fun DrawScope.sombrasEnPanos(g: MarcoUrss) {
        val e = g.panos ?: return
        val w = g.w
        val camino = e.camino(w)
        val b = camino.getBounds()
        if (b.isEmpty) return
        // El proyector y sus bobinas se dibujan agrandados desde su pie de la izquierda (ver [delante]).
        fun gr(p: Offset) = Offset(w * 0.06f + (p.x - w * 0.06f) * 1.14f, w * 0.49f + (p.y - w * 0.49f) * 1.14f)
        fun circulo(c: Offset, r: Float) = ovaloUrss(gr(c), r * 1.14f, r * 1.14f)
        val pr0 = gr(Offset(w * 0.085f, w * 0.338f)); val pr1 = gr(Offset(w * 0.452f, w * 0.488f))
        val cosas = listOf(
            Path().apply { addRect(Rect(w * 0.03f, w * 0.03f, w * 0.162f, w * 0.15f)) } to 1.2f,
            ovaloUrss(Offset(w * 0.37f, w * 0.085f), w * 0.054f, w * 0.054f) to 1f,
            circulo(Offset(w * 0.165f, w * 0.258f), w * 0.095f) to 2.6f,
            circulo(Offset(w * 0.355f, w * 0.285f), w * 0.07f) to 2.4f,
            Path().apply { addRect(Rect(pr0.x, pr0.y + (pr1.y - pr0.y) * 0.3f, pr1.x, pr1.y)) } to 1.6f,
            Path().apply {
                addRect(Rect(w * 0.562f, 0f, w * 0.568f, w * 0.176f))
                addOval(Rect(w * 0.46f, w * 0.17f, w * 0.67f, w * 0.26f))
            } to 3.6f,
        )
        val sombra = g.a.sombra(if (g.a.oscuro) 0.55f else 0.42f)
        clipRect(b.left, b.top, b.right, b.bottom) {
            clipPath(camino) {
                cosas.forEach { (p, largo) ->
                    val o = g.arrojada(largo, p.getBounds().center.x)
                    drawPath(corridoUrss(p, o.x, o.y), sombra)
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa, corrida al lado contrario de la luz. */
    private fun DrawScope.contacto(g: MarcoUrss, centro: Offset, ancho: Float, alto: Float, a: Float) {
        val l = g.luz(centro, 0.04f)
        val dx = -g.hacia(centro.x, centro.y) * ancho * 0.08f
        drawOval(l.s(a), Offset(centro.x - ancho * 0.45f + dx, centro.y - alto / 2), Size(ancho, alto))
    }

    /** La sombra de una forma en la pared, corrida según la luz del momento. */
    private fun DrawScope.enPared(g: MarcoUrss, f: Path, x: Float, largo: Float, a: Float) {
        val sa = g.arrojada(largo, x)
        clipRect(0f, 0f, g.w, g.w * 0.47f) { drawPath(corridoUrss(f, sa.x, sa.y), g.luz(x, g.w * 0.3f, 0f).s(a)) }
    }

    // ---------------------------------------------------------------- la regla en T

    /**
     * La regla en T del dibujante de carteles, apoyada en la pared en diagonal: la hoja de madera clara con su canto de celuloide, la
     * cabeza atornillada arriba y el agujero para colgarla; cruza el pliego de prueba y lo enmarca.
     */
    private fun DrawScope.regla(g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = Offset(w * 0.905f, w * 0.474f)
        val cima = Offset(w * 0.735f, w * 0.088f)
        val dir = (cima - pie) / (cima - pie).getDistance()
        val perp = Offset(-dir.y, dir.x)
        val ga = w * 0.009f
        val hoja = poligonoUrss(pie + perp * ga, cima + perp * ga, cima - perp * ga, pie - perp * ga)
        val cab0 = cima - dir * (w * 0.006f)
        val cab = poligonoUrss(cab0 + perp * (w * 0.042f), cab0 + perp * (w * 0.042f) + dir * (w * 0.016f), cab0 - perp * (w * 0.042f) + dir * (w * 0.016f), cab0 - perp * (w * 0.042f))
        val todo = Path().apply { addPath(hoja); addPath(cab) }
        enPared(g, todo, (pie.x + cima.x) / 2, 0.8f, 0.24f)
        val l = g.luz((pie + cima) / 2f, 0.03f)
        val madera = mezcla(P.ROBLE_LUZ, P.CARTULINA, 0.35f)
        drawPath(hoja, l.c(madera))
        clipPath(hoja) {
            drawPath(corridoUrss(hoja, perp.x * ga * 1.3f, perp.y * ga * 1.3f), l.b(0.35f))
            drawLine(l.c(mezcla(P.CARTULINA, P.OLEO, 0.25f)).copy(alpha = 0.7f), pie - perp * (ga * 0.6f), cima - perp * (ga * 0.6f), ga * 0.6f)
        }
        drawPath(hoja, l.s(0.3f), style = Stroke(0.8f * u))
        // Las rayitas de los centímetros a lo largo del canto de celuloide.
        val marcas = Path()
        for (j in 0 until 46) {
            val p = pie + (cima - pie) * (0.06f + 0.9f * j / 46f) - perp * ga
            marcas.moveTo(p.x, p.y)
            val q = p + perp * (ga * (if (j % 5 == 0) 0.9f else 0.45f))
            marcas.lineTo(q.x, q.y)
        }
        drawPath(marcas, l.c(P.NEGRO).copy(alpha = 0.55f), style = Stroke(0.6f * u))
        // La cabeza, más oscura, con sus dos tornillos.
        val lc = g.luz(cab0, 0.035f)
        drawPath(corridoUrss(cab, w * 0.004f, w * 0.004f), lc.s(0.3f))
        drawPath(cab, lc.c(mezcla(P.ROBLE, P.ROBLE_OSC, 0.3f)))
        drawPath(cab, lc.b(0.4f), style = Stroke(0.8f * u))
        listOf(-0.022f, 0.022f).forEach { f -> tornilloUrss(cab0 + perp * (w * f) + dir * (w * 0.008f), 1.3f * u, lc, 8001, P.METAL) }
        val hueco = pie + (cima - pie) * 0.9f
        drawCircle(lc.c(P.NEGRO).copy(alpha = 0.7f), w * 0.0035f, hueco)
    }

    // ---------------------------------------------------------------- el proyector

    /**
     * Una bobina de aluminio en su brazo: el disco de la cara de delante con sus seis agujeros redondos (se ve a través la película
     * enrollada, parda, o el fondo de la otra cara), el aro del borde con su brillo, el cubo con el eje cuadrado y el pestillo.
     */
    private fun DrawScope.bobina(g: MarcoUrss, c: Offset, r: Float, llena: Boolean, semilla: Int) {
        val w = g.w; val u = density
        val l = g.luz(c, 0.05f)
        val hacia = g.hacia(c.x, c.y)
        val disco = ovaloUrss(c, r, r)
        enPared(g, disco, c.x, 0.9f, 0.2f)
        // La película enrollada, entre las dos caras: se ve por el canto y por los agujeros.
        val pelicula = mezcla(P.PARQUET_OSC, P.NEGRO, 0.45f)
        val rp = if (llena) r * 0.86f else r * 0.42f
        drawCircle(l.c(mezcla(P.ALUMINIO, P.ACERO_OSC, 0.5f)), r, c)
        drawCircle(l.c(pelicula), rp, c)
        drawCircle(l.b(0.25f), rp, c, style = Stroke(0.8f * u))
        // La cara de delante, con sus seis agujeros.
        val agujeros = Path()
        for (j in 0 until 6) {
            val a = (j * 60f + 15f) * 0.017453f
            agujeros.addOval(Rect(c.x + cos(a) * r * 0.56f - r * 0.2f, c.y + sin(a) * r * 0.56f - r * 0.2f, c.x + cos(a) * r * 0.56f + r * 0.2f, c.y + sin(a) * r * 0.56f + r * 0.2f))
        }
        val cara = Path().apply { op(disco, agujeros, PathOperation.Difference) }
        cilindroDisco(cara, c, r, l, hacia)
        // El canto de cada agujero, en sombra arriba y con luz abajo.
        drawPath(corridoUrss(agujeros, 0f, -0.8f * u), l.s(0.35f), style = Stroke(1f * u))
        // El aro del borde, el doblez del aluminio, y su brillo del lado de la luz.
        drawCircle(l.c(mezcla(P.ALUMINIO, P.METAL, 0.4f)), r, c, style = Stroke(w * 0.006f))
        drawArc(l.b(0.8f), if (hacia < 0f) 195f else 285f, 70f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawArc(l.s(0.4f), if (hacia < 0f) 15f else 105f, 80f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.2f * u))
        drawCircle(l.s(0.3f), r * 0.86f, c, style = Stroke(0.8f * u))
        // De noche, el filo de la lámpara en el canto que la mira.
        if (l.k > 0.05f) drawArc(l.filo(0.9f), if (c.x < g.lampara.x) -40f else 140f, 80f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.6f * u))
        // El cubo: el eje cuadrado y el pestillo que sujeta la bobina.
        drawCircle(l.c(mezcla(P.ALUMINIO, P.METAL, 0.5f)), r * 0.2f, c)
        drawCircle(l.s(0.3f), r * 0.2f, c, style = Stroke(1f * u))
        drawRect(l.c(P.NEGRO), Offset(c.x - r * 0.07f, c.y - r * 0.07f), Size(r * 0.14f, r * 0.14f))
        rotate(30f + 40f * azar(semilla, 1), c) { drawRoundRect(l.c(P.METAL), Offset(c.x - r * 0.03f, c.y - r * 0.2f), Size(r * 0.06f, r * 0.16f), CornerRadius(r * 0.02f)) }
        drawCircle(l.b(0.6f), r * 0.05f, c - Offset(r * 0.08f, r * 0.08f))
    }

    /** El volumen de un disco de metal: la luz por un lado y la sombra por el otro, en diagonal. */
    private fun DrawScope.cilindroDisco(f: Path, c: Offset, r: Float, l: LuzUrss, hacia: Float) {
        val base = l.c(P.ALUMINIO)
        val claro = if (l.a.oscuro) mezcla(base, l.tinte, 0.15f + 0.5f * l.k) else mezcla(base, Color.White, 0.35f)
        val honda = mezcla(base, if (l.a.oscuro) l.a.hondo else P.ACERO_OSC, if (l.a.oscuro) 0.5f else 0.38f)
        val a = if (hacia < 0f) Offset(c.x - r, c.y - r) else Offset(c.x + r, c.y - r)
        val b = if (hacia < 0f) Offset(c.x + r, c.y + r) else Offset(c.x - r, c.y + r)
        drawPath(f, Brush.linearGradient(listOf(claro, base, base, honda), a, b))
    }

    /**
     * El proyector: el cuerpo de chapa gris verdosa con la cara de arriba, el ventanillo de la película con su compuerta, dos
     * botones de baquelita y la palanca; la torre de la lámpara con sus rejillas; el objetivo negro con su aro cromado; los dos
     * brazos de las bobinas; la película que baja de la bobina llena al ventanillo y sube a la otra; y el cable.
     */
    private fun DrawScope.proyector(g: MarcoUrss) {
        val w = g.w; val u = density
        val cuerpo = Rect(w * 0.085f, w * 0.398f, w * 0.335f, w * 0.488f)
        val torre = Rect(w * 0.29f, w * 0.338f, w * 0.372f, w * 0.488f)
        val lc = g.luz(cuerpo.center, 0.05f)
        val lt = g.luz(torre.center, 0.05f)
        val hacia = g.hacia(cuerpo.center.x, cuerpo.center.y)
        val formaC = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(w * 0.008f))) }
        val formaT = Path().apply { addRoundRect(RoundRect(torre, CornerRadius(w * 0.01f))) }
        val todo = Path().apply { addPath(formaC); addPath(formaT) }
        enPared(g, todo, cuerpo.center.x, 0.9f, 0.26f)
        contacto(g, Offset(w * 0.23f, w * 0.49f), w * 0.33f, w * 0.014f, 0.45f)
        // Los brazos de las bobinas: tubos de chapa que suben del cuerpo.
        val brazos = listOf(Offset(w * 0.2f, cuerpo.top + w * 0.004f) to Offset(w * 0.165f, w * 0.258f), Offset(w * 0.3f, torre.top + w * 0.01f) to Offset(w * 0.355f, w * 0.285f))
        brazos.forEach { (a, b) ->
            val l = g.luz((a + b) / 2f, 0.045f)
            drawLine(l.s(0.35f), a + Offset(w * 0.004f, w * 0.003f), b + Offset(w * 0.004f, w * 0.003f), w * 0.013f, StrokeCap.Round)
            drawLine(l.c(P.ACERO), a, b, w * 0.013f, StrokeCap.Round)
            drawLine(l.b(0.5f), a - Offset(w * 0.003f, 0f), b - Offset(w * 0.003f, 0f), 1.2f * u)
        }
        // La película: de la bobina llena baja al ventanillo y del ventanillo sube a la otra, con sus bucles.
        val pel = mezcla(P.PARQUET_OSC, P.NEGRO, 0.45f)
        val lp = g.luz(w * 0.27f, w * 0.36f, 0.05f)
        val baja = abiertaUrss(listOf(Offset(w * 0.245f, w * 0.29f), Offset(w * 0.262f, w * 0.34f), Offset(w * 0.25f, w * 0.38f), Offset(w * 0.262f, w * 0.405f)))
        val sube = abiertaUrss(listOf(Offset(w * 0.278f, w * 0.43f), Offset(w * 0.3f, w * 0.385f), Offset(w * 0.3f, w * 0.33f), Offset(w * 0.33f, w * 0.31f)))
        listOf(baja, sube).forEach { p ->
            drawPath(p, lp.c(pel), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawPath(p, lp.b(0.35f), style = Stroke(0.6f * u))
        }
        // El cuerpo: la chapa gris verdosa (la del fichero), en planos, y la cara de arriba.
        planosUrss(formaC, lc.c(P.ACERO), lc, hacia, 0.18f, 0.24f)
        clipPath(formaC) {
            drawRect(lc.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.5f)), cuerpo.topLeft, Size(cuerpo.width, w * 0.01f))
            drawRect(lc.b(0.6f), cuerpo.topLeft, Size(cuerpo.width, 1.2f * u))
            // El esmalte martelé: motas finas.
            for (j in 0 until 30) drawCircle(lc.s(0.12f), (0.6f + 0.8f * azar(8040, j)) * u, Offset(cuerpo.left + cuerpo.width * azar(8041, j), cuerpo.top + cuerpo.height * azar(8042, j)))
            drawRect(lc.s(0.3f), Offset(cuerpo.left, cuerpo.bottom - w * 0.008f), Size(cuerpo.width, w * 0.008f))
        }
        // El ventanillo de la película y su compuerta, con su pestillo.
        val ven = Rect(w * 0.236f, w * 0.408f, w * 0.284f, w * 0.462f)
        drawRoundRect(lc.c(P.NEGRO), ven.topLeft, ven.size, CornerRadius(w * 0.004f))
        drawRoundRect(lc.c(mezcla(P.ACERO, P.ACERO_OSC, 0.4f)), ven.topLeft + Offset(w * 0.004f, w * 0.004f), Size(ven.width - w * 0.008f, ven.height - w * 0.008f), CornerRadius(w * 0.003f))
        drawRect(lc.b(0.5f), Offset(ven.left + w * 0.004f, ven.top + w * 0.004f), Size(ven.width - w * 0.008f, 1f * u))
        drawCircle(lc.c(P.METAL), w * 0.004f, Offset(ven.right - w * 0.008f, ven.center.y))
        // Dos botones de baquelita y la palanca cromada; la chapita del modelo (bloques que no se leen).
        listOf(0.12f to 0.44f, 0.165f to 0.44f).forEach { (fx, fy) ->
            val p = Offset(w * fx, w * fy)
            drawCircle(lc.s(0.35f), w * 0.011f, p + Offset(w * 0.003f, w * 0.003f))
            drawCircle(lc.c(P.NEGRO), w * 0.011f, p)
            drawCircle(lc.b(0.5f), w * 0.004f, p - Offset(w * 0.004f, w * 0.004f))
        }
        drawLine(lc.c(P.METAL), Offset(w * 0.205f, w * 0.448f), Offset(w * 0.222f, w * 0.428f), 2.2f * u, StrokeCap.Round)
        drawCircle(lc.c(P.NEGRO), w * 0.004f, Offset(w * 0.222f, w * 0.428f))
        val placa = Rect(w * 0.105f, w * 0.462f, w * 0.18f, w * 0.475f)
        drawRect(lc.c(P.METAL), placa.topLeft, placa.size)
        drawPath(bloquesUrss(Offset(placa.left + w * 0.006f, placa.center.y - w * 0.002f), placa.width - w * 0.012f, w * 0.004f, 8043), lc.c(P.NEGRO).copy(alpha = 0.7f))
        // La torre de la lámpara, con sus rejillas de ventilación y su sombrerete.
        planosUrss(formaT, lt.c(mezcla(P.ACERO, P.ACERO_OSC, 0.15f)), lt, hacia, 0.2f, 0.3f)
        val rej = Path()
        for (j in 0 until 6) rej.addRoundRect(RoundRect(Rect(torre.left + torre.width * 0.18f, torre.top + w * (0.024f + 0.012f * j), torre.right - torre.width * 0.18f, torre.top + w * (0.03f + 0.012f * j)), CornerRadius(w * 0.003f)))
        drawPath(rej, lt.c(P.NEGRO).copy(alpha = 0.85f))
        drawPath(corridoUrss(rej, 0f, 1f * u), lt.b(0.3f), style = Stroke(0.6f * u))
        drawRoundRect(lt.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.4f)), Offset(torre.left - w * 0.004f, torre.top - w * 0.008f), Size(torre.width + w * 0.008f, w * 0.012f), CornerRadius(w * 0.004f))
        drawRect(lt.b(0.6f), Offset(torre.left - w * 0.004f, torre.top - w * 0.008f), Size(torre.width + w * 0.008f, 1f * u))
        // El objetivo: el tubo negro, el aro cromado de enfoque con sus estrías y el vidrio de delante.
        val obj = Rect(w * 0.372f, w * 0.402f, w * 0.452f, w * 0.438f)
        val lo = g.luz(obj.center, 0.06f)
        val tubo = Path().apply { addRect(obj) }
        cilindroUrssTumbado(tubo, obj, lo.c(P.NEGRO), lo)
        val aro = Rect(obj.left + obj.width * 0.3f, obj.top - w * 0.003f, obj.left + obj.width * 0.55f, obj.bottom + w * 0.003f)
        cilindroUrssTumbado(Path().apply { addRect(aro) }, aro, lo.c(P.METAL), lo)
        for (j in 0 until 6) drawLine(lo.s(0.35f), Offset(aro.left + aro.width * (j + 0.5f) / 6f, aro.top), Offset(aro.left + aro.width * (j + 0.5f) / 6f, aro.bottom), 0.7f * u)
        drawOval(lo.c(mezcla(P.OLEO_OSC, P.NEGRO, 0.5f)), Offset(obj.right - w * 0.006f, obj.top), Size(w * 0.012f, obj.height))
        drawOval(lo.b(0.7f), Offset(obj.right - w * 0.003f, obj.top + obj.height * 0.2f), Size(w * 0.004f, obj.height * 0.3f))
        // El cable, que sale de atrás y cae por detrás de la tapa.
        val cable = abiertaUrss(listOf(Offset(w * 0.09f, w * 0.47f), Offset(w * 0.06f, w * 0.475f), Offset(w * 0.045f, w * 0.471f)))
        drawPath(cable, lc.c(mezcla(P.NEGRO, P.PARQUET_OSC, 0.3f)), style = Stroke(2f * u, cap = StrokeCap.Round))
        // De noche, el filo de la lámpara colgada en la cara de arriba y en el canto que la mira.
        if (lc.k > 0.05f) {
            drawRect(lc.filo(0.8f), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, 1.4f * u))
            drawRect(lt.filo(0.9f), Offset(torre.right - 1.6f * u, torre.top), Size(1.6f * u, torre.height))
        }
    }

    /** Un cilindro tumbado (a lo largo): la luz arriba y la sombra abajo. */
    private fun DrawScope.cilindroUrssTumbado(f: Path, r: Rect, base: Color, l: LuzUrss) {
        val claro = if (l.a.oscuro) mezcla(base, l.tinte, 0.1f + 0.5f * l.k) else mezcla(base, Color.White, 0.3f)
        val honda = mezcla(base, if (l.a.oscuro) l.a.hondo else P.NEGRO, 0.35f)
        drawPath(f, Brush.verticalGradient(listOf(base, claro, base, base, honda), r.top, r.bottom))
    }

    // ---------------------------------------------------------------- las latas de película

    /**
     * Las latas de película apiladas: tres latas redondas, planas, de hojalata pintada de verde oliva y una de aluminio, una sobre
     * otra y algo corridas, con el reborde de la tapa, su brillo y la tira de papel de la de arriba (con su raya roja y renglones).
     */
    private fun DrawScope.latas(g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = w * 0.49f
        val rx = w * 0.066f; val ry = w * 0.014f; val al = w * 0.016f
        val xs = listOf(0.548f, 0.556f, 0.543f, 0.552f)
        val cols = listOf(mezcla(P.ACERO_OSC, P.VERDE, 0.3f), P.ALUMINIO, mezcla(P.ACERO, P.VERDE, 0.25f), mezcla(P.ROJO, P.GRANATE, 0.45f))
        contacto(g, Offset(w * 0.55f, pie), rx * 2.3f, w * 0.014f, 0.45f)
        val silueta = Path()
        for (i in 0 until 4) {
            val cx = w * xs[i]
            val yb = pie - i * al
            silueta.addRect(Rect(cx - rx, yb - al - ry, cx + rx, yb))
        }
        enPared(g, silueta, w * 0.5f, 0.8f, 0.22f)
        for (i in 0 until 4) {
            val cx = w * xs[i]
            val yb = pie - i * al
            val yt = yb - al
            val l = g.luz(cx, yt, 0.05f)
            val hacia = g.hacia(cx, yt)
            val costado = Path().apply {
                moveTo(cx - rx, yt); lineTo(cx - rx, yb - ry * 0.1f)
                cubicTo(cx - rx * 0.5f, yb + ry * 0.9f, cx + rx * 0.5f, yb + ry * 0.9f, cx + rx, yb - ry * 0.1f)
                lineTo(cx + rx, yt); close()
            }
            cilindroUrss(costado, l.c(cols[i]), l, hacia, 0.5f)
            // La tira de papel de una de las latas, con su raya roja y sus renglones.
            if (i == 2) {
                val tira = Rect(cx - rx * 0.32f, yb - al * 0.78f, cx + rx * 0.5f, yb - al * 0.05f)
                drawRect(l.c(P.CARTULINA_OSC), tira.topLeft, tira.size)
                drawRect(l.c(P.ROJO), tira.topLeft, Size(tira.width * 0.14f, tira.height))
                drawPath(bloquesUrss(Offset(tira.left + tira.width * 0.22f, tira.top + tira.height * 0.35f), tira.width * 0.7f, tira.height * 0.22f, 8050), l.c(P.NEGRO).copy(alpha = 0.7f))
            }
            // El reborde de la tapa, a media altura del costado.
            drawLine(l.s(0.35f), Offset(cx - rx, yt + al * 0.3f), Offset(cx + rx, yt + al * 0.3f), 1f * u)
            drawLine(l.b(0.35f), Offset(cx - rx, yt + al * 0.3f + 1f * u), Offset(cx + rx, yt + al * 0.3f + 1f * u), 0.6f * u)
            // La tapa, vista desde arriba.
            val tapa = ovaloUrss(Offset(cx, yt), rx, ry)
            drawPath(tapa, l.c(mezcla(cols[i], Color.White, 0.18f)))
            drawOval(l.s(0.2f), Offset(cx - rx * 0.7f, yt - ry * 0.7f), Size(rx * 1.4f, ry * 1.4f), style = Stroke(1f * u))
            drawArc(l.b(0.6f), 180f, 120f, false, Offset(cx - rx, yt - ry), Size(rx * 2, ry * 2), style = Stroke(1f * u))
            if (l.k > 0.05f) drawArc(l.filo(0.8f), 200f, 140f, false, Offset(cx - rx, yt - ry), Size(rx * 2, ry * 2), style = Stroke(1.2f * u))
        }
    }

    // ---------------------------------------------------------------- el vaso de té

    /**
     * El vaso de té en su podstakánnik, el del Ideario: el vaso de vidrio con el té a tres cuartos, el portavasos de metal calado
     * con su asa y su pie, y la cucharilla. Aquí, bajo la lámpara colgada: de noche el té se enciende y deja su mancha en la tapa.
     */
    private fun DrawScope.vaso(g: MarcoUrss, pie: Offset) {
        val w = g.w; val u = density
        val l = g.luz(Offset(pie.x, pie.y - w * 0.04f), 0.05f)
        val ra = w * 0.026f; val rb = w * 0.0225f
        val alto = w * 0.086f
        val yt = pie.y - alto
        val vidrio = poligonoUrss(Offset(pie.x - ra, yt), Offset(pie.x + ra, yt), Offset(pie.x + rb, pie.y - w * 0.006f), Offset(pie.x - rb, pie.y - w * 0.006f))
        enPared(g, vidrio, pie.x, 0.7f, 0.2f)
        contacto(g, Offset(pie.x + w * 0.01f, pie.y), w * 0.085f, w * 0.012f, 0.42f)
        if (g.a.oscuro) drawOval(mezcla(P.TE, P.LLAMA, 0.5f).copy(alpha = 0.32f * l.k), Offset(pie.x - w * 0.03f, pie.y - w * 0.005f), Size(w * 0.06f, w * 0.012f))
        // La cucharilla, por detrás del vidrio.
        drawLine(l.s(0.3f), Offset(pie.x - w * 0.012f, yt + w * 0.004f), Offset(pie.x + w * 0.016f, yt - w * 0.034f), w * 0.006f, StrokeCap.Round)
        drawLine(l.c(P.METAL), Offset(pie.x - w * 0.01f, yt + w * 0.006f), Offset(pie.x + w * 0.018f, yt - w * 0.036f), w * 0.0055f, StrokeCap.Round)
        drawLine(l.b(0.8f), Offset(pie.x - w * 0.012f, yt + w * 0.002f), Offset(pie.x + w * 0.016f, yt - w * 0.036f), 0.8f * u)
        // El vidrio y el té.
        drawPath(vidrio, l.c(mezcla(P.CARTULINA, P.OLEO, 0.35f)).copy(alpha = 0.55f))
        val yte = yt + alto * 0.2f
        val te = if (g.a.oscuro) mezcla(g.a.hundido(P.TE), mezcla(P.TE, P.LLAMA, 0.3f), (1.4f * l.k).coerceAtMost(1f)) else l.c(P.TE)
        clipPath(vidrio) {
            drawRect(te, Offset(pie.x - ra, yte), Size(ra * 2, alto))
            drawRect(mezcla(te, if (g.a.oscuro) P.LLAMA else Color.White, 0.35f), Offset(pie.x - ra, yte), Size(ra * 0.55f, alto))
            drawRect(l.s(0.25f), Offset(pie.x + ra * 0.45f, yte), Size(ra * 0.6f, alto))
            drawRect(l.b(0.75f), Offset(pie.x - ra * 0.72f, yt), Size(1.3f * u, alto))
        }
        drawOval(mezcla(te, P.NEGRO, 0.25f), Offset(pie.x - ra * 0.96f, yte - w * 0.004f), Size(ra * 1.92f, w * 0.008f))
        drawOval(l.b(0.7f), Offset(pie.x - ra, yt - w * 0.004f), Size(ra * 2, w * 0.008f), style = Stroke(1f * u))
        // El portavasos calado: dos filas de ventanas en arco por las que se ve el té.
        val pt = pie.y - alto * 0.6f
        val porta = poligonoUrss(Offset(pie.x - ra * 0.97f, pt), Offset(pie.x + ra * 0.97f, pt), Offset(pie.x + rb * 1.04f, pie.y - w * 0.004f), Offset(pie.x - rb * 1.04f, pie.y - w * 0.004f))
        val metal = l.c(mezcla(P.METAL, P.LATON, 0.2f))
        drawPath(porta, metal)
        clipPath(porta) {
            for (f in 0..1) for (k in 0..4) {
                val c = Offset(pie.x - ra * 0.78f + ra * 0.39f * k, pt + (pie.y - pt) * (0.36f + 0.34f * f))
                val a = ra * 0.13f; val h = (pie.y - pt) * 0.12f
                drawRoundRect(mezcla(te, P.NEGRO, 0.35f), Offset(c.x - a, c.y - h), Size(a * 2, h * 2), CornerRadius(a))
            }
            cilindroUrss(porta, Color.Transparent, l, -1f, 0f)
            drawRect(l.b(0.45f), Offset(pie.x - ra * 0.7f, pt), Size(ra * 0.25f, pie.y - pt))
            drawRect(l.s(0.3f), Offset(pie.x + ra * 0.4f, pt), Size(ra * 0.6f, pie.y - pt))
            drawRect(l.b(0.5f), Offset(pie.x - ra, pt), Size(ra * 2, 1.1f * u))
            drawRect(l.s(0.3f), Offset(pie.x - ra, pie.y - (pie.y - pt) * 0.2f), Size(ra * 2, 1f * u))
        }
        val peana = Rect(pie.x - rb * 1.25f, pie.y - w * 0.007f, pie.x + rb * 1.25f, pie.y)
        drawRoundRect(metal, peana.topLeft, peana.size, CornerRadius(w * 0.003f))
        drawRect(l.b(0.5f), peana.topLeft, Size(peana.width, 1f * u))
        val asa = Path().apply {
            moveTo(pie.x + ra * 0.95f, pt + w * 0.006f)
            cubicTo(pie.x + ra * 2.1f, pt - w * 0.004f, pie.x + ra * 2.15f, pie.y - w * 0.014f, pie.x + rb * 1.02f, pie.y - w * 0.014f)
        }
        translate(1.2f * u, 1.4f * u) { drawPath(asa, l.s(0.3f), style = Stroke(w * 0.006f, cap = StrokeCap.Round)) }
        drawPath(asa, metal, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        translate(-0.7f * u, -0.7f * u) { drawPath(asa, l.b(0.7f), style = Stroke(0.8f * u)) }
        if (l.k > 0.05f) drawLine(l.filo(0.8f), Offset(pie.x - ra * 0.9f, pt), Offset(pie.x - rb, pie.y - w * 0.006f), 1.2f * u)
    }

    // ---------------------------------------------------------------- los libros

    /**
     * Los libros recién devueltos, tumbados con el lomo hacia quien mira, cada uno de su tela, con sus filetes en seco, una banda
     * estampada y el tejuelo de la biblioteca; del de en medio asoma la ficha de préstamo.
     */
    private fun DrawScope.libros(g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = w * 0.49f
        data class L(val x0: Float, val an: Float, val al: Float, val tela: Color)
        val pila = listOf(
            L(0.735f, 0.2f, 0.034f, mezcla(P.GRANATE, P.ROJO, 0.25f)),
            L(0.748f, 0.176f, 0.028f, P.OLEO_OSC),
            L(0.728f, 0.19f, 0.031f, mezcla(P.NEGRO, P.PARQUET_OSC, 0.3f)),
        )
        var y = pie
        val cajas = pila.map { b -> Rect(w * b.x0, y - w * b.al, w * (b.x0 + b.an), y).also { y -= w * b.al } }
        val todos = Path().apply { cajas.forEach { addRect(it) } }
        enPared(g, todos, w * 0.83f, 0.8f, 0.22f)
        contacto(g, Offset(w * 0.84f, pie), w * 0.22f, w * 0.014f, 0.45f)
        cajas.forEachIndexed { i, r ->
            val l = g.luz(r.center, 0.05f)
            val tela = l.c(pila[i].tela)
            if (i > 0) drawRect(l.s(0.35f), Offset(r.left + w * 0.005f, r.bottom), Size(r.width, 2f * u))
            drawRoundRect(tela, r.topLeft, r.size, CornerRadius(w * 0.004f))
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawRect(Brush.verticalGradient(listOf(l.b(0.25f), Color.Transparent, l.s(0.3f)), r.top, r.bottom), r.topLeft, r.size)
                drawRect(l.s(0.25f), Offset(r.left, r.top + w * 0.004f), Size(r.width, 0.9f * u))
                drawRect(l.s(0.25f), Offset(r.left, r.bottom - w * 0.005f), Size(r.width, 0.9f * u))
                listOf(0.07f, 0.09f, 0.9f, 0.92f).forEach { f -> drawRect(l.s(0.28f), Offset(r.left + r.width * f, r.top + w * 0.006f), Size(1f * u, r.height - w * 0.012f)) }
                // La banda estampada, de otro color.
                drawRect(l.c(if (i == 1) P.ROJO else P.NEGRO).copy(alpha = 0.8f), Offset(r.left + r.width * (0.6f - 0.06f * i), r.top), Size(r.width * 0.06f, r.height))
                drawRect(l.b(0.2f), Offset(r.left, r.top), Size(r.width * 0.12f, r.height))
            }
            // El tejuelo de la biblioteca.
            val tj = Rect(r.left + r.width * 0.18f, r.top + r.height * 0.2f, r.left + r.width * 0.18f + w * 0.03f, r.bottom - r.height * 0.2f)
            drawRect(l.c(ETIQUETA_URSS), tj.topLeft, tj.size)
            drawRect(l.c(P.NEGRO).copy(alpha = 0.6f), tj.topLeft, tj.size, style = Stroke(0.7f * u))
            if (l.k > 0.05f) drawRect(l.filo(0.8f), r.topLeft, Size(r.width, 1.2f * u))
        }
        // La ficha de préstamo que asoma del libro de en medio, con su raya roja.
        val r = cajas[1]
        val l = g.luz(r.center, 0.05f)
        val ficha = Rect(r.right - w * 0.008f, r.top + w * 0.004f, r.right + w * 0.026f, r.top + w * 0.022f)
        drawRect(l.s(0.3f), ficha.topLeft + Offset(1f * u, 1.2f * u), ficha.size)
        drawRect(l.c(P.CARTULINA), ficha.topLeft, ficha.size)
        drawRect(l.c(P.ROJO), Offset(ficha.left, ficha.top + 1.6f * u), Size(ficha.width, 0.9f * u))
    }

    /** La mancha cálida de la lámpara en la tapa, de noche, debajo de lo que alumbra. */
    private fun DrawScope.charco(g: MarcoUrss) {
        if (g.a.lampara <= 0f) return
        mancha(Offset(g.lampara.x, g.w * 0.485f), g.w * 0.22f, P.LLAMA.copy(alpha = 0.07f * g.a.lampara))
    }
}
