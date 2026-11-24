package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * El bastidor de roble del estante de Galia ([MuebleEstanteGalia]): el dintel (el tablero de arriba, su canto, el madero
 * labrado con una hilera de triángulos rehundidos a punta de cuchillo y otra de puntos), los casilleros con el barro de almagra de la pared detrás (la sombra franca de la tabla de arriba, los
 * cantos de dentro de los postes, el piso), las tablas gruesas labradas a azuela con sus escuadras de hierro forjado, los
 * postes con su chaflán, su acanaladura y las clavijas de cada ensamble, los rótulos de haya de los meses y la solera sobre sus
 * calzos de piedra. De día, la luz viene de la puerta (izquierda); de noche, cada tramo se apaga con la distancia a la lumbre.
 */
internal object ObraEstanteGalia {
    private val P = PaletaGalia

    /** A qué alto arde el hogar, en anchos (lo apunta [tabla] para el filo de abajo de cada tabla). */
    private var abajo = 0f

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireGalia, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.035f)).toInt().coerceIn(2, 40) else 1

    /** El color de [col] con la luz del momento ([f], [k]) y, si [lit] pasa de 0, con la mancha [m] encima. */
    private fun en(a: AireGalia, col: Color, f: Float, k: Float, lit: Float, m: ManchaGalia?): Color {
        val base = a.dia(col, f, k)
        return if (lit > 0f && m != null) a.sol(col, base, m.fuerza * lit) else base
    }

    // ---------------------------------------------------------------- el dintel (en la imagen guardada de arriba)

    /** El dintel de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.dintel(r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        dintelCon(r, a, cerca, null)
        // La luz de la puerta solo enciende el tablero de arriba y su canto (lo que mira a la luz), no la cara del madero.
        val w = size.width
        if (m != null) clipPath(m.forma(w, ManchaGalia.FRENTE)) { clipRect(0f, r.top - w * 0.01f, w, r.top + w * 0.026f) { dintelCon(r, a, cerca, m) } }
    }

    private fun DrawScope.dintelCon(r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, k: Float) = en(a, col, f, k, lit, m)
        val y0 = r.top
        val sup0 = y0 - w * 0.008f; val sup1 = y0 + w * 0.008f
        val canto1 = y0 + w * 0.024f
        val cara0 = canto1 + w * 0.008f
        val talla0 = cara0 + w * 0.018f; val talla1 = talla0 + w * 0.032f
        val pie0 = r.bottom - 2f - w * 0.012f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y0 + w * 0.04f))
            // El tablero de arriba, visto desde encima (más claro y gastado), y su canto.
            drawRect(c(P.ROBLE_LUZ, -0.06f, k), Offset(x0, sup0), Size(x1 - x0, sup1 - sup0))
            drawRect(c(P.ROBLE, 0.05f, k), Offset(x0, sup1), Size(x1 - x0, canto1 - sup1))
            // La sombra del tablero en el madero, y el madero.
            drawRect(c(P.ROBLE_HONDO, 0f, k), Offset(x0, canto1), Size(x1 - x0, cara0 - canto1))
            drawRect(c(P.ROBLE, 0f, k), Offset(x0, cara0), Size(x1 - x0, pie0 - cara0 + 1f))
            // El listón de abajo, que remata contra las repisas.
            drawRect(c(P.ROBLE_OSC, 0f, k), Offset(x0, pie0), Size(x1 - x0, r.bottom - pie0))
        }
        // Los filetes de luz y las líneas de sombra.
        drawRect(a.claro(0.45f), Offset(0f, sup1), Size(w, 1.1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, canto1 - 1f * u), Size(w, 1f * u))
        drawRect(a.claro(0.25f), Offset(0f, cara0 + 0.4f * u), Size(w, 0.9f * u))
        drawRect(a.claro(0.3f), Offset(0f, pie0), Size(w, 0.9f * u))
        drawRect(a.sombra(0.5f), Offset(0f, r.bottom - 2f - 2.4f * u), Size(w, 2.4f * u))
        // Los golpes de la azuela en el madero y la veta.
        val golpes = Path(); val filos = Path(); val vetas = Path()
        var x = w * 0.01f; var j = 0
        while (x < w) {
            val ya = cara0 + (pie0 - cara0) * azar(9010, j) * 0.5f
            val l = (pie0 - cara0) * (0.25f + 0.3f * azar(9011, j))
            golpes.moveTo(x, ya); golpes.lineTo(x + w * 0.008f, ya + l)
            filos.moveTo(x + w * 0.005f, ya); filos.lineTo(x + w * 0.013f, ya + l)
            x += w * (0.03f + 0.03f * azar(9012, j)); j++
        }
        for (q in 0 until 3) {
            val yy = cara0 + (pie0 - cara0) * (0.2f + 0.3f * q)
            vetas.moveTo(0f, yy)
            var xx = 0f; var s = 0
            while (xx < w) { xx += w * 0.05f; vetas.lineTo(xx, yy + (azar(9020 + q, s) - 0.5f) * 2.2f * u); s++ }
        }
        val km = cerca(Offset(w * 0.8f, cara0))
        drawPath(vetas, a.luz(P.ROBLE_HONDO, km).copy(alpha = 0.3f), style = Stroke(0.7f * u))
        drawPath(golpes, a.sombra(0.28f), style = Stroke(1f * u))
        drawPath(filos, a.brillo(0.18f, km), style = Stroke(0.7f * u))
        // La talla: una hilera de triángulos rehundidos a punta de cuchillo, cada uno con su faceta de luz y sus dos de sombra,
        // entre dos filetes; y debajo una hilera de puntos hundidos con el punzón.
        val luces = Path(); val sombras = Path(); val hondos = Path()
        val paso = w * 0.04f
        var xt = w * MuebleEstanteGalia.LADO + w * 0.004f
        val xFin = w * (1f - MuebleEstanteGalia.LADO) - paso * 0.9f
        while (xt < xFin) {
            val cx = xt + paso * 0.5f
            val cy = talla0 + (talla1 - talla0) * 0.62f
            // El triángulo con la punta abajo y, entre dos, el de la punta arriba: así la talla es una banda de facetas.
            luces.moveTo(xt, talla0); luces.lineTo(cx, cy); luces.lineTo(cx, talla1); luces.lineTo(xt + paso * 0.08f, talla1); luces.close()
            sombras.moveTo(xt + paso, talla0); sombras.lineTo(cx, cy); sombras.lineTo(cx, talla1); sombras.lineTo(xt + paso * 0.92f, talla1); sombras.close()
            hondos.moveTo(xt, talla0); hondos.lineTo(xt + paso, talla0); hondos.lineTo(cx, cy); hondos.close()
            xt += paso
        }
        drawPath(hondos, a.sombra(0.42f))
        drawPath(luces, a.brillo(0.16f, km))
        drawPath(sombras, a.sombra(0.2f))
        drawRect(a.sombra(0.4f), Offset(w * MuebleEstanteGalia.LADO, talla0 - 1.2f * u), Size(w * (1f - 2 * MuebleEstanteGalia.LADO), 1.2f * u))
        drawRect(a.brillo(0.3f, km), Offset(w * MuebleEstanteGalia.LADO, talla1), Size(w * (1f - 2 * MuebleEstanteGalia.LADO), 0.9f * u))
        val puntos = Path()
        var xp = w * MuebleEstanteGalia.LADO + paso * 0.5f
        val yp = talla1 + w * 0.012f
        while (xp < xFin + paso * 0.5f) { puntos.addOval(Rect(xp - w * 0.0035f, yp - w * 0.0035f, xp + w * 0.0035f, yp + w * 0.0035f)); xp += paso * 0.5f }
        drawPath(puntos, a.sombra(0.5f))
        // El gastado: golpes claros en el canto del tablero y una mancha de grasa.
        for (q in 0 until 8) {
            val xg = w * (0.05f + 0.9f * azar(9030, q))
            drawPath(poligonoGalia(xg, sup1, xg + w * 0.014f, sup1, xg + w * 0.007f, sup1 + w * 0.006f), c(P.ROBLE_LUZ, 0.12f, cerca(Offset(xg, sup1))))
        }
        // Los clavos de hierro donde el dintel se clava a los postes, y una pletina en la junta del medio.
        listOf(w * MuebleEstanteGalia.LADO * 0.5f, w * (1f - MuebleEstanteGalia.LADO * 0.5f)).forEach { xc ->
            val k = cerca(Offset(xc, cara0))
            listOf(0.3f, 0.72f).forEach { fy -> clavoGalia(Offset(xc, cara0 + (pie0 - cara0) * fy), 2.6f * u, a, k) }
        }
        val pl = Rect(w * 0.48f, cara0 + w * 0.002f, w * 0.52f, pie0 - w * 0.002f)
        val kp = cerca(pl.center)
        drawRect(a.sombra(0.4f), Offset(pl.left + 1.2f * u, pl.top + 1.2f * u), pl.size)
        drawRect(c(P.HIERRO, 0f, kp), pl.topLeft, pl.size)
        drawRect(a.brillo(0.3f, kp), pl.topLeft, Size(pl.width, 0.9f * u))
        listOf(0.18f, 0.82f).forEach { fy -> clavoGalia(Offset(pl.center.x, pl.top + pl.height * fy), 1.8f * u, a, kp) }
        // De noche, el filo de la tea en el canto del tablero, por tramos.
        if (a.oscuro && m == null) {
            for (t in 0 until n) {
                val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
                drawRect(a.filo((0.9f * cerca(Offset((x0 + x1) / 2, y0))).coerceAtMost(0.75f)), Offset(x0, sup1), Size(x1 - x0, 1.3f * u))
            }
        }
    }

    // ---------------------------------------------------------------- los casilleros

    /** El casillero [r] de la repisa [k]: el barro de almagra de la pared, sus sombras, los cantos de dentro y el piso. */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        fondo(k, r, a, cerca, null)
        if (m != null) clipPath(m.forma(size.width, 0f)) { fondo(k, r, a, cerca, m) }
    }

    private fun DrawScope.fondo(k: Int, r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        // El barro rojo se hunde de noche en vino; la madera, en roble ahumado.
        val av = AireGalia(a.oscuro, a.noche, cuerpo = true, fondo = true)
        fun cf(col: Color, f: Float, kk: Float) = en(av, col, f, kk, lit, m)
        fun c(col: Color, f: Float, kk: Float) = en(a, col, f, kk, lit, m)
        // El barro de almagra, por tramos: de noche, cada uno con su luz. Un píxel de más arriba (salvo en la primera, que
        // toca la imagen de la corona), abajo y a los lados, que tapan la tabla y los postes: así ninguna junta a medio píxel
        // deja asomar el fondo.
        val n = tramos(a, r.width, w)
        val y0 = if (k == 0) r.top else floor(r.top) - 1f
        val y1 = ceil(r.bottom) + 1f
        for (t in 0 until n) {
            val x0 = if (t == 0) floor(r.left) - 1f else r.left + r.width * t / n
            val x1 = if (t == n - 1) ceil(r.right) + 1f else r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            drawRect(cf(P.ALMAGRA, 0f, kk), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        }
        val km = cerca(r.center)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // La almagra dada a mano: brochazos más claros y más oscuros, las pasadas de la mano y las grietas del barro.
            val claros = Path(); val oscuros = Path(); val grietas = Path()
            for (q in 0 until 12) {
                val bx = r.left + r.width * azar(9100 + k, q); val by = r.top + r.height * (0.25f + 0.65f * azar(9110 + k, q))
                val l = w * (0.04f + 0.06f * azar(9120 + k, q))
                (if (q % 2 == 0) claros else oscuros).addOval(Rect(bx, by, bx + l, by + l * 0.22f))
            }
            for (g in 0 until 2) {
                var q = Offset(r.left + r.width * azar(9130 + k, g), r.top + r.height * (0.3f + 0.3f * azar(9131 + k, g)))
                grietas.moveTo(q.x, q.y)
                for (i in 0 until 7) { q += Offset((azar(9132 + k + g, i) - 0.5f) * w * 0.016f, w * (0.006f + 0.008f * azar(9140 + k + g, i))); grietas.lineTo(q.x, q.y) }
            }
            drawPath(claros, cf(P.ALMAGRA_LUZ, 0f, km).copy(alpha = 0.32f))
            drawPath(oscuros, cf(P.ALMAGRA_OSC, 0f, km).copy(alpha = 0.32f))
            drawPath(grietas, cf(P.ALMAGRA_HONDA, 0f, km).copy(alpha = 0.7f), style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // Un desconchón donde se cayó la almagra y asoma el barro (en una repisa de cada dos), con su canto de luz.
            if (k % 2 == 0) {
                val dx = r.left + r.width * (0.3f + 0.4f * azar(9150, k)); val dy = r.top + r.height * (0.32f + 0.2f * azar(9151, k))
                val dr = Rect(dx, dy, dx + w * 0.1f, dy + w * 0.04f)
                val forma = parcheGalia(dr, 9152 + k)
                // Sin canto marcado: con él, el parche se lee como una cosa colgada (un plato) y no como el barro gastado.
                drawPath(forma, cf(mezcla(P.ALMAGRA, P.BARRO, 0.26f), -0.05f, cerca(dr.center)).copy(alpha = 0.85f))
                drawPath(forma, a.sombra(0.16f), style = Stroke(0.7f * u))
            }
            // La sombra franca de la tabla de arriba (y de lo que ella guarda) y la del poste izquierdo (la luz viene de ahí).
            val franca = w * 0.05f
            drawRect(a.sombra(if (a.oscuro) 0.55f else 0.45f), r.topLeft, Size(r.width, franca))
            drawRect(a.sombra(0.18f), Offset(r.left, r.top + franca), Size(r.width, w * 0.025f))
            drawRect(a.sombra(0.1f), Offset(r.left, r.top + franca + w * 0.025f), Size(r.width, w * 0.022f))
            if (!a.oscuro) {
                drawPath(poligonoGalia(r.left, r.top, r.left + w * 0.06f, r.top, r.left + w * 0.04f, r.bottom, r.left, r.bottom), a.sombra(0.32f))
            } else {
                drawRect(a.sombra(0.35f), Offset(r.left, r.top), Size(w * 0.03f, r.height))
                drawRect(a.sombra(0.35f), Offset(r.right - w * 0.03f, r.top), Size(w * 0.03f, r.height))
            }
            // El cielo del casillero: la cara de abajo de la tabla de arriba, en escorzo.
            val cielo = w * 0.012f
            drawRect(c(P.ROBLE_HONDO, 0f, km), r.topLeft, Size(r.width, cielo))
            // Los cantos de dentro de los postes: el izquierdo en sombra, el derecho a la luz (de día).
            val e = w * 0.022f
            val kI = cerca(Offset(r.left, r.center.y)); val kD = cerca(Offset(r.right, r.center.y))
            drawPath(poligonoGalia(r.left, r.top, r.left + e, r.top + cielo, r.left + e, r.bottom - w * 0.016f, r.left, r.bottom), c(P.ROBLE_OSC, -0.1f, kI))
            drawPath(poligonoGalia(r.right, r.top, r.right - e, r.top + cielo, r.right - e, r.bottom - w * 0.016f, r.right, r.bottom), c(P.ROBLE, 0.12f, kD))
            drawLine(a.claro(0.28f), Offset(r.right - e, r.top + cielo), Offset(r.right - e, r.bottom - w * 0.016f), 0.8f * u)
            // El piso: la cara de arriba de la tabla de abajo, en escorzo, gastada.
            val piso = w * 0.018f
            for (t in 0 until n) {
                val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
                drawRect(c(P.ROBLE_LUZ, -0.14f, cerca(Offset((x0 + x1) / 2, r.bottom))), Offset(x0, r.bottom - piso), Size(minOf(x1, r.right) - x0, piso))
            }
            drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - piso), Size(r.width, 1.2f * u))
            // Unas briznas de paja y unos granos caídos en el piso.
            val briznas = Path()
            for (q in 0 until 6) {
                val bx = r.left + r.width * azar(9160 + k, q); val by = r.bottom - piso * (0.2f + 0.6f * azar(9161 + k, q))
                briznas.moveTo(bx, by); briznas.lineTo(bx + w * (0.01f + 0.012f * azar(9162 + k, q)), by - w * 0.002f)
            }
            drawPath(briznas, c(P.JUNCO, 0f, km).copy(alpha = 0.8f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla [r]: su canto grueso labrado a azuela, la arista de arriba, las vetas, los golpes y las escuadras de hierro. */
    fun DrawScope.tabla(k: Int, r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width
        // Debajo, la fila entera de la junta con la repisa de arriba (de píxel entero), para que no asome el fondo.
        val fy = floor(r.top)
        drawRect(a.luz(P.ROBLE, cerca(r.center)), Offset(r.left, fy), Size(r.width, ceil(r.top + 1f) - fy))
        tablaCon(k, r, a, cerca, null)
        // La luz de la puerta solo enciende la arista de arriba de la tabla.
        if (m != null) clipPath(m.forma(w, ManchaGalia.FRENTE)) { clipRect(r.left, r.top, r.right, r.top + r.height * 0.2f) { tablaCon(k, r, a, cerca, m) } }
    }

    private fun DrawScope.tablaCon(k: Int, r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, kk: Float) = en(a, col, f, kk, lit, m)
        val h = r.height
        val n = tramos(a, r.width, w)
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            val ww = minOf(x1, r.right) - x0
            // La arista de arriba, a la luz; la cara; el chaflán de abajo, en sombra. Cada franja baja un píxel más y la
            // siguiente la tapa: las juntas a medio píxel no dejan pasar el fondo.
            drawRect(c(P.ROBLE_LUZ, 0.05f, kk), Offset(x0, r.top), Size(ww, h * 0.14f + 1f))
            drawRect(c(P.ROBLE, 0.02f, kk), Offset(x0, r.top + h * 0.14f), Size(ww, h * 0.66f + 1f))
            drawRect(c(P.ROBLE_OSC, 0f, kk), Offset(x0, r.top + h * 0.8f), Size(ww, h * 0.2f))
        }
        val km = cerca(r.center)
        drawRect(a.brillo(0.55f, km), r.topLeft, Size(r.width, 1.1f * u))
        if (a.oscuro && m == null) {
            // De noche, el filo cálido de los cantos que miran a la lumbre: arriba, el de la lamparilla y la tea; abajo, el
            // del hogar, que alumbra desde el pie.
            for (t in 0 until n) {
                val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
                val ww = minOf(x1, r.right) - x0
                val kk = cerca(Offset((x0 + x1) / 2, r.top))
                drawRect(a.filo((0.9f * kk).coerceAtMost(0.75f)), Offset(x0, r.top), Size(ww, 1.4f * u))
                val kb = MuebleEstanteGalia.hogar((x0 + x1) / 2 / w - MuebleEstanteGalia.HOGAR_X, r.bottom / w - abajo, a.noche)
                if (kb > 0.05f) drawRect(a.filo((1.1f * kb).coerceAtMost(0.7f)), Offset(x0, r.bottom - 2.2f * u), Size(ww, 1.4f * u))
            }
        }
        drawRect(a.sombra(0.32f), Offset(r.left, r.top + h * 0.14f), Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.38f), Offset(r.left, r.top + h * 0.8f), Size(r.width, 0.9f * u))
        // Las vetas y los golpes de la azuela, de través.
        val vetas = Path(); val golpes = Path(); val filos = Path()
        for (j in 0 until 3) {
            val yy = r.top + h * (0.26f + 0.18f * j)
            vetas.moveTo(r.left, yy)
            var x = r.left; var q = 0
            while (x < r.right) { x += w * 0.055f; vetas.lineTo(x, yy + (azar(9200 + k * 7 + j, q) - 0.5f) * 1.8f * u); q++ }
        }
        var xg = r.left + w * 0.01f * azar(9210, k); var j = 0
        while (xg < r.right) {
            val ya = r.top + h * (0.18f + 0.2f * azar(9211 + k, j)); val l = h * (0.3f + 0.25f * azar(9212 + k, j))
            golpes.moveTo(xg, ya); golpes.lineTo(xg + w * 0.006f, ya + l)
            filos.moveTo(xg + w * 0.004f, ya); filos.lineTo(xg + w * 0.01f, ya + l)
            xg += w * (0.028f + 0.03f * azar(9213 + k, j)); j++
        }
        drawPath(vetas, a.luz(P.ROBLE_HONDO, km).copy(alpha = 0.35f), style = Stroke(0.7f * u))
        drawPath(golpes, a.sombra(0.26f), style = Stroke(0.9f * u))
        drawPath(filos, a.brillo(0.16f, km), style = Stroke(0.6f * u))
        // El gastado del uso: el canto de arriba más claro donde se sacan y se meten las cosas, golpes y un nudo.
        val gx = r.left + r.width * (0.2f + 0.4f * azar(9220, k))
        drawRoundRect(c(P.ROBLE_LUZ, 0.18f, cerca(Offset(gx, r.top))).copy(alpha = 0.55f), Offset(gx, r.top + 0.6f * u), Size(r.width * 0.3f, h * 0.1f), CornerRadius(h * 0.05f))
        for (q in 0 until 6) {
            val x = r.left + r.width * azar(9230 + k, q)
            drawPath(poligonoGalia(x, r.top, x + w * 0.013f, r.top, x + w * 0.006f, r.top + h * 0.2f), c(P.ROBLE_LUZ, 0.15f, cerca(Offset(x, r.top))))
        }
        val nx = r.left + r.width * (0.1f + 0.8f * azar(9240, k))
        drawOval(a.luz(P.ROBLE_HONDO, km).copy(alpha = 0.7f), Offset(nx, r.top + h * 0.36f), Size(w * 0.022f, h * 0.26f))
        drawOval(a.brillo(0.2f, km), Offset(nx + w * 0.005f, r.top + h * 0.42f), Size(w * 0.008f, h * 0.1f))
        // Las escuadras de hierro forjado de las puntas, que doblan sobre el poste, con sus clavos de cabeza gruesa.
        listOf(r.left - w * 0.012f, r.right - w * 0.045f).forEach { x ->
            val kk = cerca(Offset(x, r.center.y))
            val pl = Rect(x, r.top + h * 0.2f, x + w * 0.057f, r.bottom - h * 0.2f)
            drawRect(a.sombra(0.38f), Offset(pl.left + 1f * u, pl.top + 1.2f * u), pl.size)
            drawRect(c(P.HIERRO, 0f, kk), pl.topLeft, pl.size)
            drawRect(a.brillo(0.3f, kk), pl.topLeft, Size(pl.width, 0.8f * u))
            // Las marcas del martillo en la pletina.
            drawLine(a.sombra(0.3f), Offset(pl.left + pl.width * 0.45f, pl.top + 1f * u), Offset(pl.left + pl.width * 0.5f, pl.bottom - 1f * u), 0.7f * u)
            listOf(0.22f, 0.78f).forEach { fx -> clavoGalia(Offset(pl.left + pl.width * fx, pl.center.y), 1.6f * u, a, kk, P.HIERRO) }
        }
        // La línea de su canto bajo (la sombra de la tabla en el casillero de abajo la pinta el casillero).
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- los postes

    /** Un poste [r]: el roble con su chaflán, la acanaladura, las clavijas de cada ensamble y la arista roída por las manos. */
    fun DrawScope.poste(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        abajo = (d.base.top + d.base.height * MuebleEstanteGalia.HOGAR_Y) / size.width
        // Debajo, las filas enteras de las juntas con el dintel y la solera.
        drawRect(a.luz(P.ROBLE, cerca(r.center)), Offset(floor(r.left), floor(r.top)), Size(ceil(r.right) - floor(r.left), ceil(r.bottom) - floor(r.top)))
        posteCon(r, izq, d, a, cerca, null)
        if (m != null) clipPath(m.forma(size.width, ManchaGalia.FRENTE)) { posteCon(r, izq, d, a, cerca, m) }
    }

    private fun DrawScope.posteCon(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, kk: Float) = en(a, col, f, kk, lit, m)
        val pw = r.width
        // Las franjas del poste, de fuera adentro: el chaflán de fuera, la cara, la acanaladura, la cara, el chaflán de dentro.
        val fr = floatArrayOf(0f, 0.14f, 0.44f, 0.54f, 0.84f, 1f)
        val tonos = listOf(P.ROBLE_LUZ to 0.04f, P.ROBLE to 0f, P.ROBLE_HONDO to 0f, P.ROBLE to 0.03f, P.ROBLE_OSC to 0f)
        val trozos = if (a.lumbre) maxOf(2, (r.height / (w * 0.06f)).toInt()) else 1
        for (t in 0 until trozos) {
            val y0 = r.top + r.height * t / trozos; val y1 = r.top + r.height * (t + 1) / trozos + 1.2f
            val kk = cerca(Offset(r.center.x, (y0 + y1) / 2))
            for (i in 0 until 5) {
                val fa = fr[i]; val fb = fr[i + 1]
                val xa = if (izq) r.left + pw * fa else r.right - pw * fb
                val (col, f) = tonos[i]
                // La luz del día viene de la izquierda: el chaflán de fuera del poste derecho queda en sombra y el de dentro, a la luz.
                val ff = if (!izq && i == 0) -0.12f else if (!izq && i == 4) 0.1f else f
                drawRect(c(col, ff, kk), Offset(xa, y0), Size(pw * (fb - fa) + 1.2f, minOf(y1, r.bottom) - y0))
            }
        }
        val km = cerca(r.center)
        // Las líneas de la talla: el fondo de la acanaladura y sus dos filos.
        val xs = if (izq) listOf(pw * 0.44f, pw * 0.54f) else listOf(pw * 0.56f, pw * 0.46f)
        drawLine(a.sombra(0.5f), Offset(r.left + xs[0], r.top), Offset(r.left + xs[0], r.bottom), 1f * u)
        drawLine(a.claro(0.3f), Offset(r.left + xs[1], r.top), Offset(r.left + xs[1], r.bottom), 0.8f * u)
        // Los golpes de la azuela, de arriba abajo, y una grieta de secado.
        val golpes = Path(); val filos = Path()
        var y = r.top + w * 0.01f; var j = 0
        while (y < r.bottom) {
            val xa = r.left + pw * (if (izq) 0.06f + 0.3f * azar(9300, j) else 0.58f + 0.3f * azar(9300, j))
            val l = w * (0.02f + 0.025f * azar(9301, j))
            golpes.moveTo(xa, y); golpes.lineTo(xa + pw * 0.06f, y + l)
            filos.moveTo(xa + pw * 0.05f, y); filos.lineTo(xa + pw * 0.11f, y + l)
            y += w * (0.035f + 0.03f * azar(9302 + (if (izq) 0 else 1), j)); j++
        }
        drawPath(golpes, a.sombra(0.26f), style = Stroke(1f * u))
        drawPath(filos, a.brillo(0.16f, km), style = Stroke(0.7f * u))
        val gx = r.left + pw * (if (izq) 0.26f else 0.7f)
        val grieta = Path().apply {
            moveTo(gx, r.top + r.height * 0.12f)
            var gy = r.top + r.height * 0.12f; var q = 0
            while (gy < r.top + r.height * 0.42f) { gy += w * 0.02f; lineTo(gx + (azar(9310 + (if (izq) 0 else 3), q) - 0.5f) * 1.6f * u, gy); q++ }
        }
        drawPath(grieta, a.luz(P.ROBLE_HONDO, km).copy(alpha = 0.8f), style = Stroke(1f * u, cap = StrokeCap.Round))
        // Por repisa: la arista roída por las manos y las dos clavijas del ensamble de cada tabla.
        d.repisas.forEachIndexed { jr, rp ->
            val kk = cerca(Offset(r.center.x, rp.center.y))
            val xx = if (izq) r.left + pw * 0.8f else r.left + pw * 0.06f
            drawOval(c(P.ROBLE_LUZ, 0.14f, kk).copy(alpha = 0.4f), Offset(xx, rp.top + rp.height * 0.25f), Size(pw * 0.14f, rp.height * 0.5f))
            if (azar(9320 + jr, if (izq) 1 else 2) > 0.5f) {
                val ny = rp.top + rp.height * (0.2f + 0.5f * azar(9321 + jr, if (izq) 1 else 2))
                val nx = r.left + pw * (if (izq) 0.62f else 0.24f)
                drawOval(c(P.ROBLE_HONDO, 0f, kk).copy(alpha = 0.7f), Offset(nx, ny), Size(pw * 0.16f, pw * 0.26f))
                drawOval(c(P.ROBLE_LUZ, 0f, kk).copy(alpha = 0.45f), Offset(nx + pw * 0.04f, ny + pw * 0.06f), Size(pw * 0.06f, pw * 0.11f))
            }
        }
        d.tablas.forEach { tb ->
            val kk = cerca(Offset(r.center.x, tb.center.y))
            listOf(0.3f, 0.72f).forEach { fx ->
                val p = Offset(r.left + pw * fx, tb.center.y)
                // La clavija de roble, de testa: la sombra, la cabeza con la luz y la veta de testa en un anillo.
                drawCircle(a.sombra(0.45f), pw * 0.11f, p + Offset(0.7f * u, 0.9f * u))
                drawCircle(c(P.ROBLE_LUZ, -0.05f, kk), pw * 0.1f, p)
                drawCircle(a.luz(P.ROBLE_OSC, kk).copy(alpha = 0.6f), pw * 0.055f, p, style = Stroke(0.6f * u))
                drawCircle(a.brillo(0.4f, kk), pw * 0.035f, p - Offset(pw * 0.035f, pw * 0.035f))
            }
            // La cuña del ensamble, metida de testa entre las dos clavijas.
            val cu = Rect(r.left + pw * 0.46f, tb.top + tb.height * 0.22f, r.left + pw * 0.56f, tb.bottom - tb.height * 0.22f)
            drawRect(a.sombra(0.4f), Offset(cu.left + 0.8f * u, cu.top + 0.8f * u), cu.size)
            drawRect(c(P.ROBLE_LUZ, -0.08f, kk), cu.topLeft, cu.size)
        }
        // La sombra que el poste echa hacia dentro y el filo de su canto que mira al hueco; de noche, ese canto se enciende con
        // la lumbre que tiene enfrente (por tramos, más donde le llega más).
        if (izq) drawRect(a.sombra(0.25f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        else drawRect(a.claro(0.2f), Offset(r.left, r.top), Size(1.2f * u, r.height))
        if (a.oscuro && m == null) {
            val n = maxOf(2, (r.height / (w * 0.06f)).toInt())
            val xf = if (izq) r.right - 1.8f * u else r.left
            for (t in 0 until n) {
                val y0 = r.top + r.height * t / n; val y1 = r.top + r.height * (t + 1) / n + 1.2f
                val kk = cerca(Offset(if (izq) r.right + w * 0.05f else r.left - w * 0.05f, (y0 + y1) / 2))
                drawRect(a.filo((0.9f * kk).coerceAtMost(0.7f)), Offset(xf, y0), Size(1.8f * u, minOf(y1, r.bottom) - y0))
            }
        }
    }

    // ---------------------------------------------------------------- los rótulos de los meses

    /** El rótulo del mes: una tablilla de haya de esquinas cortadas, clavada en el canto de la tabla, con el mes rayado. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireGalia, k: Float) {
        val w = size.width; val u = density
        val c = p.r.center
        val ancho = minOf(w * 0.66f, (p.r.width / 0.42f) * 0.74f)
        val r = Rect(c.x - ancho / 2, p.r.top - p.r.height * 0.06f, c.x + ancho / 2, p.r.bottom + p.r.height * 0.06f)
        val corte = r.height * 0.28f
        val forma = poligonoGalia(r.left + corte, r.top, r.right - corte * 0.8f, r.top + 0.4f * u, r.right, r.top + corte, r.right - 0.4f * u, r.bottom - corte * 0.7f,
            r.right - corte, r.bottom, r.left + corte * 0.9f, r.bottom - 0.3f * u, r.left, r.bottom - corte, r.left + 0.3f * u, r.top + corte * 0.8f)
        drawPath(Path().apply { addPath(forma, Offset(1.4f * u, 1.8f * u)) }, a.sombra(0.4f))
        val haya = a.etiqueta(P.HAYA, k)
        drawPath(forma, haya)
        clipPath(forma) {
            drawRect(a.etiqueta(P.HAYA_LUZ, k).copy(alpha = 0.55f), r.topLeft, Size(r.width, r.height * 0.22f))
            drawRect(a.etiqueta(P.HAYA_OSC, k).copy(alpha = 0.5f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
            // Las motas de la haya, cortas y oscuras, a lo largo de la veta.
            val motas = Path()
            for (q in 0 until 26) {
                val mx = r.left + r.width * azar(9400 + p.texto.length, q); val my = r.top + r.height * azar(9401 + p.texto.length, q)
                motas.moveTo(mx, my); motas.lineTo(mx + w * (0.004f + 0.006f * azar(9402, q)), my)
            }
            drawPath(motas, a.etiqueta(P.HAYA_MOTA, k).copy(alpha = 0.45f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        drawPath(forma, a.luz(P.ROBLE_HONDO, k).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        clavoGalia(Offset(r.left + 5f * u, r.center.y), 1.7f * u, a, k)
        clavoGalia(Offset(r.right - 5f * u, r.center.y), 1.7f * u, a, k)
        // El mes, rayado en la haya: el surco oscuro y, corrido, el labio claro.
        val caja = Rect(r.left + 11f * u, r.top, r.right - 11f * u, r.bottom)
        if (!a.oscuro) textoEn(medidor, p.texto, caja.translate(0.5f * u, 0.6f * u), P.LETRA, a.claro(0.5f), 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
        textoEn(medidor, p.texto, caja, P.LETRA, if (a.oscuro) Color(0xFF140C06) else a.luz(P.TINTA, maxOf(k, 0.7f)), 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
    }

    // ---------------------------------------------------------------- la solera

    /** La solera de [r]: el madero de abajo labrado a azuela, sus clavijas y los calzos de piedra sobre los que pisa. */
    fun DrawScope.solera(r: Rect, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val h = r.height
        val y1 = r.top + h * 0.7f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            drawRect(a.dia(P.ROBLE_LUZ, 0.02f, kk), Offset(x0, r.top), Size(x1 - x0, h * 0.16f + 1f))
            drawRect(a.dia(P.ROBLE, 0f, kk), Offset(x0, r.top + h * 0.16f), Size(x1 - x0, y1 - r.top - h * 0.16f + 1f))
            // Debajo del madero, la sombra honda entre los calzos.
            drawRect(a.dia(P.ROBLE_HONDO, 0f, kk), Offset(x0, y1), Size(x1 - x0, r.bottom - y1 + 1f))
        }
        val km = cerca(r.center)
        drawRect(a.brillo(0.45f, km), Offset(0f, r.top + 0.6f * u), Size(w, 1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, r.top + h * 0.16f), Size(w, 1f * u))
        drawRect(a.sombra(0.45f), Offset(0f, y1 - 2f * u), Size(w, 2f * u))
        val golpes = Path(); val filos = Path()
        var x = w * 0.01f; var j = 0
        while (x < w) {
            val ya = r.top + h * (0.2f + 0.2f * azar(9500, j)); val l = h * (0.25f + 0.2f * azar(9501, j))
            golpes.moveTo(x, ya); golpes.lineTo(x + w * 0.008f, ya + l)
            filos.moveTo(x + w * 0.005f, ya); filos.lineTo(x + w * 0.013f, ya + l)
            x += w * (0.03f + 0.03f * azar(9502, j)); j++
        }
        drawPath(golpes, a.sombra(0.26f), style = Stroke(1f * u))
        drawPath(filos, a.brillo(0.16f, km), style = Stroke(0.7f * u))
        // Las clavijas donde los postes entran en la solera.
        listOf(w * MuebleEstanteGalia.LADO * 0.5f, w * (1f - MuebleEstanteGalia.LADO * 0.5f)).forEach { xc ->
            val kk = cerca(Offset(xc, r.center.y))
            val p = Offset(xc, r.top + h * 0.42f)
            drawCircle(a.sombra(0.45f), w * 0.009f, p + Offset(0.7f * u, 0.9f * u))
            drawCircle(a.dia(P.ROBLE_LUZ, -0.05f, kk), w * 0.0085f, p)
            drawCircle(a.brillo(0.4f, kk), w * 0.003f, p - Offset(w * 0.003f, w * 0.003f))
        }
        // Los calzos: piedras planas bajo los postes y en medio, que asoman del suelo.
        listOf(0.01f, 0.45f, 0.9f).forEachIndexed { i, fx ->
            val x0 = w * fx; val pw = w * (0.08f + 0.02f * azar(9510, i))
            val kk = cerca(Offset(x0 + pw / 2, r.bottom))
            val piedra = poligonoGalia(x0, r.bottom + 1f, x0 + pw * 0.06f, y1, x0 + pw * 0.9f, y1 + 0.6f * u, x0 + pw, r.bottom + 1f)
            drawPath(piedra, a.dia(P.PIEDRA, 0f, kk))
            clipPath(piedra) {
                drawRect(a.claro(0.3f), Offset(x0, y1), Size(pw, (r.bottom - y1) * 0.3f))
                drawRect(a.sombra(0.3f), Offset(x0 + pw * 0.7f, y1), Size(pw * 0.3f, r.bottom - y1 + 1f))
            }
        }
        drawRect(a.sombra(0.5f), Offset(0f, y1), Size(w, 1.4f * u), blendMode = BlendMode.SrcOver)
    }
}
