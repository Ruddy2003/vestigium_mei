package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La librería de roble de la biblioteca municipal: la cornisa (el cornisón con su gola, los dentellones, el friso de tableros
 * rehundidos sobre las pilastras y el astrágalo), los costados en pilastra con su tablero, las bisagras de latón de las puertas
 * que ya no están, las baldas de canto moldurado con su portaetiquetas y el farolillo de lata colgado del costado derecho.
 */
internal object MuebleRuinas {
    /** Dónde se toma la luz a lo ancho, para los degradados de lo que cruza el mueble de lado a lado. */
    private val MUESTRAS = listOf(0.02f, 0.14f, 0.27f, 0.42f, 0.58f, 0.76f, 0.98f)

    // ---------------------------------------------------------------- la cornisa (se pinta una vez, en la corona guardada)

    /**
     * La cornisa, de [MarcoRuinas.apoyo] al pie de la corona: cada moldura con su luz (la mancha del lucernario la pinta con borde
     * neto), los dentellones que se cayeron bajo la gotera, el helecho que salió en la grieta de la izquierda, musgo en el
     * cornisón y la mancha de la gotera que baja por el friso.
     */
    fun DrawScope.cornisa(g: MarcoRuinas) {
        val w = g.w; val u = density
        val y0 = g.apoyo; val ch = g.ch
        val alto = ch - y0
        fun y(f: Float) = y0 + alto * f
        conManchaRuinas(g, 0.06f, Rect(0f, y0, w, ch)) { s ->
            fun l(x: Float, yy: Float) = g.luz(x, yy, 0.06f, s)
            // Debajo de todas las molduras, el roble entero: las juntas entre una moldura y otra caen a medio píxel y así no dejan
            // asomar el fondo.
            drawRect(Brush.horizontalGradient(MUESTRAS.map { l(w * it, y0 + alto * 0.5f).c(P.ROBLE_OSC) }, 0f, w), Offset(0f, y0), Size(w, ch - y0))
            fun banda(f0: Float, f1: Float, col: (Float) -> Color) {
                val a = y(f0); val b = y(f1)
                drawRect(Brush.horizontalGradient(MUESTRAS.map { col(w * it) }, 0f, w), Offset(0f, a), Size(w, b - a + 0.6f))
            }
            // El cornisón: la cara de arriba (donde pisan las cosas), su canto a la luz y la gola en sombra.
            banda(0f, 0.07f) { x -> mezcla(l(x, y(0.03f)).c(P.ROBLE_LUZ), P.GASTADO, 0.25f) }
            banda(0.07f, 0.17f) { x -> l(x, y(0.12f)).c(P.ROBLE) }
            drawLine(l(w * 0.3f, y(0.07f)).b(0.55f), Offset(0f, y(0.075f)), Offset(w, y(0.075f)), 1.2f * u)
            val g0 = y(0.17f); val g1 = y(0.3f)
            banda(0.17f, 0.3f) { x -> l(x, g1).c(P.ROBLE) }
            drawRect(Brush.verticalGradient(listOf(g.a.sombra(0.55f), g.a.sombra(0.15f), Color.Transparent), g0, g1), Offset(0f, g0), Size(w, g1 - g0 + 0.6f))
            drawRect(Brush.horizontalGradient(MUESTRAS.map { l(w * it, g1).b(0.4f) }, 0f, w), Offset(0f, g1 - (g1 - g0) * 0.3f), Size(w, (g1 - g0) * 0.3f))
            // Los dentellones: tacos con su luz arriba a la izquierda y el hueco hondo entre ellos.
            val d0 = y(0.3f); val d1 = y(0.45f)
            banda(0.3f, 0.45f) { x -> mezcla(l(x, d0).c(P.ROBLE_OSC), honda(g), 0.45f) }
            val paso = w * 0.034f
            var x = w * 0.006f
            var n = 0
            while (x < w) {
                val lx = l(x, d0)
                // A la derecha, bajo el cuenco de la gotera, el agua pudrió tres dentellones y se cayeron; uno quedó torcido.
                if (x > w * 0.7f && x < w * 0.8f) {
                    if (n % 3 == 1) rotate(14f, Offset(x, d0)) { drawRect(lx.c(mezcla(P.ROBLE, P.GASTADO, 0.4f)), Offset(x, d0), Size(paso * 0.6f, (d1 - d0) * 0.8f)) }
                    x += paso; n++; continue
                }
                drawRect(lx.c(P.ROBLE), Offset(x, d0), Size(paso * 0.6f, d1 - d0))
                drawRect(lx.b(0.35f), Offset(x, d0), Size(paso * 0.6f, 1.2f * u))
                drawRect(lx.s(0.3f), Offset(x + paso * 0.45f, d0), Size(paso * 0.15f, d1 - d0))
                x += paso; n++
            }
            // El filete y el friso: liso entre las pilastras, con dos tableros rehundidos; sobre cada pilastra, su dado.
            banda(0.45f, 0.5f) { xx -> l(xx, y(0.47f)).c(P.ROBLE_LUZ) }
            banda(0.5f, 0.78f) { xx -> l(xx, y(0.64f)).c(P.ROBLE) }
            val f0 = y(0.5f); val f1 = y(0.78f)
            val x0 = g.x0; val x1 = g.x1
            for ((a, b) in listOf(x0 + w * 0.02f to w * 0.5f - w * 0.03f, w * 0.5f + w * 0.03f to x1 - w * 0.02f)) {
                val t = Rect(a, f0 + (f1 - f0) * 0.2f, b, f1 - (f1 - f0) * 0.2f)
                val lt = l(t.center.x, t.center.y)
                drawRect(mezcla(lt.c(P.ROBLE), honda(g), 0.18f), t.topLeft, t.size)
                drawRect(lt.s(0.4f), t.topLeft, Size(t.width, 1.6f * u))
                drawRect(lt.s(0.3f), t.topLeft, Size(1.6f * u, t.height))
                drawRect(lt.b(0.4f), Offset(t.left, t.bottom - 1.2f * u), Size(t.width, 1.2f * u))
                drawRect(lt.b(0.3f), Offset(t.right - 1.2f * u, t.top), Size(1.2f * u, t.height))
                vetas(t, lt.c(P.ROBLE_OSC).copy(alpha = 0.35f), 1001 + a.toInt(), 0.5f, grosor = 0.6f)
            }
            // Los dados sobre las pilastras: un poco salientes, con su canto a la luz y su sombra a un lado.
            for ((a, b) in listOf(0f to x0, x1 to w)) {
                val ld = l((a + b) / 2, (f0 + f1) / 2)
                drawRect(ld.c(P.ROBLE_LUZ), Offset(a, f0), Size(b - a, f1 - f0))
                drawRect(ld.c(P.ROBLE), Offset(a + (b - a) * 0.18f, f0 + (f1 - f0) * 0.18f), Size((b - a) * 0.64f, (f1 - f0) * 0.64f))
                drawRect(ld.s(0.35f), Offset(if (a < w / 2) b - 2f * u else a, f0), Size(2f * u, f1 - f0))
            }
            // El astrágalo (un junquillo redondo) y la banda de abajo.
            val a0 = y(0.78f); val a1 = y(0.88f)
            banda(0.78f, 0.88f) { x -> l(x, a0).c(P.ROBLE) }
            drawRect(Brush.horizontalGradient(MUESTRAS.map { l(w * it, a0).b(0.45f) }, 0f, w), Offset(0f, a0), Size(w, (a1 - a0) * 0.35f))
            drawRect(g.a.sombra(0.4f), Offset(0f, a1 - (a1 - a0) * 0.3f), Size(w, (a1 - a0) * 0.3f))
            banda(0.88f, 1f) { xx -> mezcla(l(xx, y(0.94f)).c(P.ROBLE_OSC), honda(g), 0.2f) }
            drawLine(l(w * 0.4f, y(0.88f)).b(0.3f), Offset(0f, y(0.885f)), Offset(w, y(0.885f)), 0.8f * u)
        }
        // El barniz comido por el agua y las vetas a lo largo, en toda la cornisa.
        vetas(Rect(0f, y(0.07f), w, y(0.17f)), g.luz(w * 0.5f, y(0.1f), 0.03f).c(P.ROBLE_OSC).copy(alpha = 0.35f), 1011, 0.6f, grosor = 0.6f)
        for (i in 0 until 6) {
            val c = Offset(w * (0.1f + 0.8f * azar(1021, i)), y(0.55f + 0.2f * azar(1022, i)))
            drawOval(g.luz(c.x, c.y, 0.03f).c(P.GASTADO).copy(alpha = 0.22f), Offset(c.x - w * 0.05f, c.y - w * 0.008f), Size(w * 0.1f, w * 0.016f))
        }
        // La mancha de la gotera, que baja por el friso desde el cuenco.
        val xg = w * ParedEstanteRuinas.GOTERA_X
        drawRect(Brush.verticalGradient(listOf(honda(g).copy(alpha = 0.3f), honda(g).copy(alpha = 0.05f)), y(0.1f), ch), Offset(xg - w * 0.03f, y(0.1f)), Size(w * 0.05f, ch - y(0.1f)))
        // La grieta del cornisón donde se cayeron los dentellones.
        val lg = g.luz(w * 0.75f, y(0.1f), 0.03f)
        drawPath(abiertaRuinas(listOf(Offset(w * 0.71f, y0 + 1f), Offset(w * 0.73f, y(0.12f)), Offset(w * 0.75f, y(0.2f)), Offset(w * 0.77f, y(0.3f)))), lg.s(0.6f), style = Stroke(1.4f * u))
        // El helecho de la grieta (a la izquierda) y el musgo del cornisón.
        val lh = g.luz(w * 0.055f, y0, 0.04f, ParedEstanteRuinas.solEn(g, Offset(w * 0.055f, y0), 0.04f))
        val hacia = if (g.a.oscuro && !g.a.noche) 1f else -1f
        listOf(-38f to 0.16f, -12f to 0.2f, 18f to 0.15f, 42f to 0.11f).forEachIndexed { i, (ang, lar) ->
            frondaRuinas(Offset(w * 0.055f, y0 + alto * 0.2f), w * lar, ang, 0.35f * (if (ang < 0f) -1f else 1f), if (i % 2 == 0) P.HELECHO else mezcla(P.HELECHO, P.HIEDRA, 0.4f), lh, hacia, 1030 + i, 10)
        }
        listOf(0.3f to 0.07f, 0.58f to 0.05f, 0.9f to 0.06f).forEachIndexed { i, (fx, an) ->
            val b = Offset(w * fx, y0 + alto * 0.08f)
            musgoRuinas(b, w * an, w * 0.012f, g.luz(b.x, b.y, 0.03f, ParedEstanteRuinas.solEn(g, b, 0.03f)), 1040 + i)
        }
    }

    /** Lo hondo de la madera: el oscuro con color en que se hunden las sombras. */
    private fun honda(g: MarcoRuinas): Color = ParedEstanteRuinas.honduraRuinas(g)

    // ---------------------------------------------------------------- las juntas

    /**
     * Las filas de las juntas: donde una tabla toca el hueco de arriba o de abajo, el borde puede caer a medio píxel. Debajo de
     * las dos piezas va esa fila entera, de píxel entero y del color de la tabla, solo a lo ancho de las dos; y lo mismo en las
     * columnas donde los costados tocan los huecos.
     */
    fun DrawScope.juntas(g: MarcoRuinas, d: DisposicionEstante) {
        val oscuro = P.ROBLE_OSC
        d.tablas.forEach { r ->
            for (yy in listOf(r.top, r.bottom)) {
                val f = floor(yy)
                drawRect(g.luz(g.w * 0.5f, yy, 0.03f).c(oscuro), Offset(r.left - 1f, f), Size(r.width + 2f, 1f))
            }
        }
        val y0 = floor(g.ch); val y1 = ceil(g.fondo)
        for (xx in listOf(g.x0, g.x1)) {
            val f = floor(xx)
            drawRect(g.luz(xx, (y0 + y1) / 2, 0.03f).c(oscuro), Offset(f, y0), Size(1f, y1 - y0))
        }
    }

    // ---------------------------------------------------------------- las baldas

    /**
     * Una balda: el canto moldurado de roble (el bocel redondo arriba, la cara y el filete de abajo), gastado y con algo de musgo;
     * en medio, el portaetiquetas de latón (la tarjeta del mes la pone [placa]; sin mes, vacío).
     */
    fun DrawScope.balda(k: Int, r: Rect, g: MarcoRuinas) {
        val w = g.w; val u = density
        fun l(x: Float) = g.luz(x, r.center.y, 0.03f)
        val cols = List(7) { r.left + r.width * it / 6f }
        conManchaRuinas(g, 0.05f, r) { s ->
            fun ls(x: Float) = g.luz(x, r.center.y, 0.05f, s)
            drawRect(Brush.horizontalGradient(cols.map { ls(it).c(P.ROBLE) }, r.left, r.right), r.topLeft, r.size)
            // El bocel de arriba: la luz en lo alto, la sombra debajo.
            val b1 = r.top + r.height * 0.3f
            drawRect(Brush.verticalGradient(listOf(ls(r.center.x).c(P.ROBLE_LUZ), ls(r.center.x).c(P.ROBLE), ls(r.center.x).c(P.ROBLE_OSC)), r.top, b1), r.topLeft, Size(r.width, b1 - r.top))
        }
        drawLine(l(r.left + r.width * 0.2f).b(0.5f), Offset(r.left, r.top + 1f * u), Offset(r.right, r.top + 1f * u), 1f * u)
        // La sombra de debajo del canto (el filete de abajo) y la que echa en el hueco siguiente la pone su fondo.
        drawRect(Brush.horizontalGradient(cols.map { mezcla(l(it).c(P.ROBLE_OSC), honda(g), 0.3f) }, r.left, r.right), Offset(r.left, r.bottom - r.height * 0.16f), Size(r.width, r.height * 0.16f))
        drawLine(l(r.center.x).b(0.25f), Offset(r.left, r.bottom - r.height * 0.18f), Offset(r.right, r.bottom - r.height * 0.18f), 0.7f * u)
        // Las vetas y el desgaste: el barniz comido en manchas claras y un cerco oscuro.
        vetas(Rect(r.left, r.top + r.height * 0.3f, r.right, r.bottom - r.height * 0.18f), l(r.center.x).c(P.ROBLE_OSC).copy(alpha = 0.4f), 1100 + k, 0.55f, grosor = 0.6f)
        val s = 1110 + k * 7
        for (i in 0 until 2) {
            val c = Offset(r.left + r.width * azar(s, i), r.center.y)
            drawOval(l(c.x).c(P.GASTADO).copy(alpha = 0.3f), Offset(c.x - w * 0.05f, c.y - r.height * 0.18f), Size(w * 0.1f, r.height * 0.36f))
        }
        // De noche, el filo de las brasas del anafre en el canto de abajo, que mira al suelo: más fuerte cuanto más cerca.
        if (g.a.oscuro && g.a.anafre > 0f) {
            val xs = List(7) { r.left + r.width * it / 6f }
            val filos = xs.map { x -> P.FUEGO.copy(alpha = (0.9f * g.a.anafre * MuebleEstanteRuinas.kAnafre(x / w, r.bottom / w, g.h / w)).coerceIn(0f, 0.9f)) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, r.left, r.right), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.6f * u))
        }
        // Los filos de la luz de la llama en el canto.
        val lmax = cols.maxByOrNull { l(it).k } ?: r.center.x
        if (l(lmax).k > 0.08f) drawLine(l(lmax).filo(0.7f), Offset(maxOf(r.left, lmax - w * 0.25f), r.top + 1.4f * u), Offset(minOf(r.right, lmax + w * 0.25f), r.top + 1.4f * u), 1.4f * u)
        // Musgo en el canto, en uno o dos sitios.
        for (i in 0 until 2) {
            if (azar(s, 20 + i) < 0.45f) continue
            val x = r.left + r.width * (0.06f + 0.88f * azar(s, 30 + i))
            if (kotlin.math.abs(x - r.center.x) < r.width * 0.26f) continue
            musgoRuinas(Offset(x, r.top + r.height * 0.5f), w * (0.03f + 0.03f * azar(s, 40 + i)), r.height * 0.5f, l(x), s + i)
        }
        // El portaetiquetas de latón, vacío cuando la tabla no lleva mes (con la tarjeta, lo dibuja [placa]).
        if (g.n > 0) portaetiquetas(Rect(r.center.x - w * 0.09f, r.top + r.height * 0.18f, r.center.x + w * 0.09f, r.bottom - r.height * 0.2f), g, vacio = true)
    }

    /** El marco de latón del portaetiquetas: la chapa con sus pestañas, dos tornillos, su brillo y el cardenillo. */
    private fun DrawScope.portaetiquetas(r: Rect, g: MarcoRuinas, vacio: Boolean) {
        val u = density
        val l = g.luz(r.center.x, r.center.y, 0.035f)
        val marco = r.inflate(2.2f * u)
        drawRect(l.s(0.4f), marco.topLeft + Offset(1.2f * u, 1.4f * u), marco.size)
        drawRect(l.c(P.LATON), marco.topLeft, marco.size)
        drawRect(l.c(P.LATON_OSC), Offset(marco.left, marco.bottom - 1.2f * u), Size(marco.width, 1.2f * u))
        drawLine(l.b(0.7f), Offset(marco.left, marco.top + 0.6f * u), Offset(marco.right, marco.top + 0.6f * u), 0.9f * u)
        // El cardenillo en una esquina.
        mancha(Offset(marco.right - 3f * u, marco.bottom - 2f * u), 5f * u, l.c(P.CARDENILLO).copy(alpha = 0.6f))
        if (vacio) {
            drawRect(mezcla(l.c(P.ROBLE_OSC), honda(g), 0.3f), r.topLeft, r.size)
            drawRect(l.s(0.4f), r.topLeft, Size(r.width, 1.4f * u))
        }
        for (x in listOf(marco.left + 2.4f * u, marco.right - 2.4f * u)) {
            drawCircle(l.c(P.LATON_OSC), 1.3f * u, Offset(x, marco.center.y))
            drawCircle(l.b(0.6f), 0.5f * u, Offset(x - 0.4f * u, marco.center.y - 0.4f * u))
        }
        if (l.k > 0.08f) drawLine(l.filo(0.8f), Offset(marco.left, marco.top + 0.6f * u), Offset(marco.right, marco.top + 0.6f * u), 1.2f * u)
    }

    /** La tarjeta del mes en su portaetiquetas: cartón con el texto a lápiz, que de noche se tiñe con la luz que le toca. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoRuinas) {
        val u = density
        val r = p.r
        portaetiquetas(r.deflate(0.5f * u), g, vacio = false)
        val l = g.luz(r.center.x, r.center.y, 0.035f)
        val carton = if (g.a.oscuro) mezcla(mezcla(P.CARTON, g.a.hondo, 0.4f * (1f - l.k)), l.tinte, 0.3f * l.k) else l.c(P.CARTON)
        drawRect(carton, r.topLeft, r.size)
        // El cartón con sus manchas de agua y la sombra del borde de arriba del marco.
        drawOval(mezcla(carton, P.KRAFT_OSC, 0.4f).copy(alpha = 0.25f), Offset(r.left + r.width * 0.62f, r.top + r.height * 0.2f), Size(r.width * 0.3f, r.height * 0.7f))
        drawRect(l.s(0.35f), r.topLeft, Size(r.width, 1.6f * u))
        val tinta = if (g.a.oscuro) Color(0xFF14110D) else P.LAPIZ
        textoEn(medidor, p.texto, r.deflate(2f * u), P.LETRA_MES, tinta, 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Los dos costados en pilastra: la cara de roble con su tablero rehundido, el junquillo del canto de dentro, las bisagras de
     * latón de las puertas de vidrio que ya no están (en una queda un trozo astillado del bastidor), el musgo al pie
     * del izquierdo y la hiedra que trepa por el derecho.
     */
    fun DrawScope.costados(g: MarcoRuinas) {
        val w = g.w
        pilastra(g, 0f, g.x0, izquierda = true)
        pilastra(g, g.x1, w, izquierda = false)
        hiedraDelCostado(g)
    }

    private fun DrawScope.pilastra(g: MarcoRuinas, a: Float, b: Float, izquierda: Boolean) {
        val w = g.w; val u = density
        val y0 = g.ch; val y1 = g.fondo
        if (y1 - y0 < 2f) return
        val xc = (a + b) / 2
        // La luz a lo largo: unos puntos (la llama cae poco a poco).
        val pasos = 10
        fun col(c: Color, dx: Float = 0f) = List(pasos + 1) { i -> g.luz(xc + dx, y0 + (y1 - y0) * i / pasos, 0.03f).c(c) }
        drawRect(Brush.verticalGradient(col(P.ROBLE), y0, y1), Offset(a, y0), Size(b - a, y1 - y0))
        // El canto exterior (el borde del mueble), más oscuro; el junquillo de dentro, con su luz.
        val an = b - a
        val fuera = if (izquierda) a else b - an * 0.12f
        drawRect(Brush.verticalGradient(col(P.ROBLE_OSC), y0, y1), Offset(fuera, y0), Size(an * 0.12f, y1 - y0))
        val junq = if (izquierda) b - an * 0.16f else a
        drawRect(Brush.verticalGradient(col(P.ROBLE_LUZ), y0, y1), Offset(junq, y0), Size(an * 0.16f, y1 - y0))
        drawRect(Brush.verticalGradient(List(pasos + 1) { i -> g.luz(junq, y0 + (y1 - y0) * i / pasos, 0.03f).s(0.35f) }, y0, y1),
            Offset(junq + an * 0.1f, y0), Size(an * 0.06f, y1 - y0))
        // El tablero rehundido: su fondo, la sombra del bisel de arriba y del lado de la luz, y el canto de abajo y del otro lado a la luz.
        val t = Rect(a + an * 0.24f, y0 + w * 0.03f, b - an * 0.28f, y1 - w * 0.03f)
        if (t.height > 4f * u) {
            drawRect(Brush.verticalGradient(col(mezcla(P.ROBLE, P.ROBLE_OSC, 0.4f)), t.top, t.bottom), t.topLeft, t.size)
            vetas(t, g.luz(xc, (y0 + y1) / 2, 0.03f).c(P.ROBLE_OSC).copy(alpha = 0.45f), if (izquierda) 1201 else 1202, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 1.6f)
            drawRect(Brush.verticalGradient(List(pasos + 1) { i -> g.luz(xc, y0 + (y1 - y0) * i / pasos, 0.03f).s(0.4f) }, t.top, t.bottom), t.topLeft, Size(1.8f * u, t.height))
            drawRect(Brush.verticalGradient(List(pasos + 1) { i -> g.luz(xc, y0 + (y1 - y0) * i / pasos, 0.03f).b(0.35f) }, t.top, t.bottom), Offset(t.right - 1.4f * u, t.top), Size(1.4f * u, t.height))
            drawRect(g.luz(xc, t.top, 0.03f).s(0.45f), t.topLeft, Size(t.width, 1.8f * u))
            drawRect(g.luz(xc, t.bottom, 0.03f).b(0.3f), Offset(t.left, t.bottom - 1.2f * u), Size(t.width, 1.2f * u))
        }
        // Los filos de la llama a lo largo del canto que la mira.
        val filoX = if (izquierda) b - 1.2f * u else a + 1.2f * u
        drawRect(Brush.verticalGradient(List(pasos + 1) { i -> g.luz(filoX, y0 + (y1 - y0) * i / pasos, 0.03f).filo(0.75f) }, y0, y1), Offset(filoX - 0.7f * u, y0), Size(1.4f * u, y1 - y0))
        // El agua que chorreó por la cara: dos rastros más claros y uno oscuro.
        for (i in 0 until 3) {
            val x = a + an * (0.3f + 0.2f * i)
            val largo = (y1 - y0) * (0.2f + 0.3f * azar(if (izquierda) 1210 else 1211, i))
            val c = if (i == 1) honda(g).copy(alpha = 0.18f) else g.luz(x, y0, 0.03f).c(P.GASTADO).copy(alpha = 0.18f)
            drawRect(c, Offset(x, y0), Size(an * 0.08f, largo))
        }
        // Las bisagras de latón de las puertas que ya no están, en el canto de dentro.
        val bx = if (izquierda) b - an * 0.1f else a + an * 0.1f
        listOf(y0 + w * 0.05f, y1 - w * 0.09f).forEachIndexed { i, yb -> bisagra(Offset(bx, yb), g, izquierda, roto = izquierda && i == 0) }
        // El musgo al pie de la izquierda.
        if (izquierda) {
            musgoRuinas(Offset(xc, y1), an * 0.9f, w * 0.03f, g.luz(xc, y1, 0.03f), 1220)
            musgoRuinas(Offset(xc - an * 0.2f, y1 - w * 0.08f), an * 0.4f, w * 0.014f, g.luz(xc, y1, 0.03f), 1221)
        }
    }

    /** Una bisagra de latón con su nudo y dos tornillos; en la [roto], queda un trozo del bastidor de la puerta, astillado. */
    private fun DrawScope.bisagra(c: Offset, g: MarcoRuinas, izquierda: Boolean, roto: Boolean) {
        val w = g.w; val u = density
        val l = g.luz(c.x, c.y, 0.04f)
        val al = w * 0.05f; val an = w * 0.016f
        val hoja = Rect(if (izquierda) c.x - an else c.x, c.y - al / 2, if (izquierda) c.x else c.x + an, c.y + al / 2)
        drawRect(l.s(0.4f), hoja.topLeft + Offset(1.4f * u, 1.4f * u), hoja.size)
        drawRect(l.c(P.LATON_OSC), hoja.topLeft, hoja.size)
        drawRect(l.c(P.LATON), hoja.topLeft, Size(hoja.width * 0.6f, hoja.height))
        // El nudo (el cilindro del pasador), con su luz.
        val nudo = Rect(c.x - 1.8f * u, hoja.top - 1.5f * u, c.x + 1.8f * u, hoja.bottom + 1.5f * u)
        drawRect(Brush.horizontalGradient(listOf(l.c(P.LATON), l.c(Color(0xFFF0D48A)), l.c(P.LATON_OSC)), nudo.left, nudo.right), nudo.topLeft, nudo.size)
        for (k in 0..2) drawLine(l.s(0.4f), Offset(nudo.left, nudo.top + nudo.height * k / 2.5f), Offset(nudo.right, nudo.top + nudo.height * k / 2.5f), 0.6f * u)
        for (k in 0 until 2) drawCircle(l.c(P.LATON_OSC), 1.1f * u, Offset(hoja.center.x, hoja.top + hoja.height * (0.25f + 0.5f * k)))
        mancha(Offset(hoja.center.x, hoja.bottom - 2f * u), 4f * u, l.c(P.CARDENILLO).copy(alpha = 0.55f))
        if (l.k > 0.08f) drawLine(l.filo(0.8f), Offset(nudo.left, nudo.top), Offset(nudo.left, nudo.bottom), 1f * u)
        if (roto) {
            // El trozo del bastidor de la puerta, astillado.
            val lado = if (izquierda) 1f else -1f
            val x0 = c.x + lado * 1.8f * u
            val trozo = poligonoRuinas(Offset(x0, hoja.top - 2f * u), Offset(x0 + lado * w * 0.022f, hoja.top - 2f * u), Offset(x0 + lado * w * 0.026f, hoja.top + al * 0.3f),
                Offset(x0 + lado * w * 0.016f, hoja.top + al * 0.5f), Offset(x0 + lado * w * 0.02f, hoja.bottom + 3f * u), Offset(x0, hoja.bottom + 3f * u))
            drawPath(corridoRuinas(trozo, 2f * u, 2f * u), l.s(0.4f))
            drawPath(trozo, l.c(P.ROBLE))
            drawPath(bordeRuinas(trozo, -lado * 1.5f * u, 0f), l.b(0.4f))
        }
    }

    /** La hiedra que trepa por el costado derecho desde el suelo, con sus raicillas pegadas a la madera. */
    private fun DrawScope.hiedraDelCostado(g: MarcoRuinas) {
        val w = g.w
        val y1 = g.fondo + w * 0.04f
        val alto = (y1 - g.ch) * 0.62f
        if (alto < w * 0.05f) return
        val n = ((alto / (w * 0.05f)).toInt()).coerceIn(3, 26)
        val hacia = if (g.a.oscuro) 1f else -1f
        val pts = List(n + 1) { i ->
            val t = i / n.toFloat()
            Offset(w * (0.945f + 0.03f * kotlin.math.sin(t * 9f) - 0.02f * t), y1 - alto * t)
        }
        talloRuinas(pts, w * 0.04f, 1230, { p -> g.luz(p.x, p.y, 0.05f) }, hacia, 1, 1.6f)
        val pts2 = List(n / 2 + 1) { i ->
            val t = i / (n / 2f)
            Offset(w * (0.925f + 0.025f * kotlin.math.cos(t * 7f)), y1 - alto * 0.55f * t)
        }
        talloRuinas(pts2, w * 0.034f, 1231, { p -> g.luz(p.x, p.y, 0.05f) }, hacia, 1, 1.2f)
    }

    // ---------------------------------------------------------------- el farolillo

    /**
     * El farolillo de lata colgado de un gancho del costado derecho, con cuerpo de farol: el pie con su borde, el cuerpo de
     * hojalata vieja agujereado en arcos, la puertecilla abierta que deja ver la vela, el sombrerete cónico con su chimenea y la
     * anilla de donde cuelga. De noche la vela arde: asoma por la puertecilla y por cada agujero, enciende su halo en la madera y
     * siembra puntos de luz alrededor.
     */
    fun DrawScope.farolillo(g: MarcoRuinas) {
        val w = g.w; val u = density
        if (g.n == 0) return
        val c = g.farolillo
        val an = w * 0.058f; val al = w * 0.078f
        val cuerpo = Rect(c.x - an / 2, c.y - al * 0.45f, c.x + an / 2, c.y + al * 0.45f)
        val tope = cuerpo.top - al * 0.42f
        val anilla = Offset(c.x, tope - 3.6f * u)
        val gancho = Offset(c.x, anilla.y - 3.4f * u)
        val l = g.luz(c.x, c.y, 0.06f)
        val arde = g.a.farolillo
        val hondo = honda(g)
        // El halo en la madera y los puntos de luz que siembra.
        if (arde > 0f) {
            mancha(c, w * 0.16f, P.LLAMA.copy(alpha = 0.32f * arde))
            for (i in 0 until 22) {
                val ang = 6.2832f * azar(1301, i); val d = w * (0.05f + 0.1f * azar(1302, i))
                val p = c + Offset(kotlin.math.cos(ang) * d, kotlin.math.sin(ang) * d * 1.3f)
                drawCircle(P.LLAMA.copy(alpha = (0.75f - d / (w * 0.17f)).coerceIn(0.12f, 0.7f) * arde), (0.8f + 1f * azar(1303, i)) * u, p)
            }
        } else {
            // De día, su sombra en la pilastra, corrida a la derecha y abajo.
            drawRoundRect(l.s(0.35f), Offset(cuerpo.left + w * 0.012f, tope + w * 0.01f), Size(an, cuerpo.bottom - tope + al * 0.08f), CornerRadius(an * 0.2f))
        }
        // El gancho clavado en la pilastra y la anilla.
        drawLine(l.c(P.HIERRO), gancho - Offset(2.6f * u, 0f), gancho, 1.8f * u, StrokeCap.Round)
        drawLine(l.c(P.HIERRO), gancho, gancho + Offset(0f, 1.8f * u), 1.6f * u, StrokeCap.Round)
        drawCircle(l.c(P.HIERRO_LUZ), 2.6f * u, anilla, style = Stroke(1.3f * u))
        // El cuerpo de hojalata vieja (mate, con óxido), cilíndrico: su luz, su sombra y las costuras.
        val lata = mezcla(P.NIQUEL, P.HIERRO_LUZ, 0.4f)
        val ci = if (arde > 0f) mezcla(l.c(lata), P.LLAMA, 0.12f) else l.c(lata)
        val borde = { r: Rect -> drawRect(Brush.horizontalGradient(listOf(mezcla(ci, Color.White, 0.2f), ci, mezcla(ci, hondo, 0.55f)), r.left, r.right), r.topLeft, r.size) }
        borde(cuerpo)
        drawRect(l.c(P.BARRO_OSC).copy(alpha = 0.5f), Offset(cuerpo.left, cuerpo.bottom - al * 0.14f), Size(an, al * 0.14f))
        // Los agujeros en arcos, a un lado de la puertecilla.
        val claro = if (arde > 0f) Color(0xFFFFE9A8) else mezcla(P.HIERRO, hondo, 0.4f)
        val rr = maxOf(0.75f * u, an * 0.045f)
        for (fila in 0 until 4) for (col in 0 until 2) {
            val x = cuerpo.left + an * (0.16f + 0.16f * col)
            val y = cuerpo.top + al * (0.16f + 0.2f * fila) + al * 0.04f * col
            if (arde > 0f) drawCircle(P.LLAMA.copy(alpha = 0.4f * arde), rr * 2.3f, Offset(x, y))
            drawCircle(claro, rr, Offset(x, y))
        }
        // La puertecilla abierta: el hueco con la vela dentro, y la hoja de lata girada hacia fuera sobre su bisagra.
        val hueco = Rect(cuerpo.left + an * 0.48f, cuerpo.top + al * 0.14f, cuerpo.right - an * 0.08f, cuerpo.bottom - al * 0.16f)
        drawRect(if (arde > 0f) mezcla(P.FUEGO, P.LLAMA, 0.5f) else mezcla(l.c(P.HIERRO), hondo, 0.3f), hueco.topLeft, hueco.size)
        val vela = Rect(hueco.center.x - hueco.width * 0.22f, hueco.bottom - hueco.height * 0.5f, hueco.center.x + hueco.width * 0.22f, hueco.bottom)
        drawRect(if (arde > 0f) Color(0xFFFFE6B0) else l.c(Color(0xFFE6DAB8)), vela.topLeft, vela.size)
        drawLine(l.c(P.HIERRO), Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 1.6f * u), 0.8f * u)
        if (arde > 0f) {
            val f = Offset(vela.center.x, vela.top - hueco.height * 0.16f)
            drawPath(cerradaRuinas(listOf(Offset(f.x, f.y - hueco.height * 0.16f), Offset(f.x + hueco.width * 0.18f, f.y + hueco.height * 0.04f), Offset(f.x, f.y + hueco.height * 0.1f), Offset(f.x - hueco.width * 0.18f, f.y + hueco.height * 0.04f))), Color(0xFFFFF4C8))
        }
        val hoja = poligonoRuinas(Offset(hueco.right, hueco.top), Offset(hueco.right + an * 0.42f, hueco.top + al * 0.05f), Offset(hueco.right + an * 0.42f, hueco.bottom + al * 0.05f), Offset(hueco.right, hueco.bottom))
        drawPath(hoja, mezcla(ci, hondo, 0.3f))
        drawPath(hoja, l.s(0.3f), style = Stroke(0.8f * u))
        drawLine(l.c(P.HIERRO_LUZ), Offset(hueco.right, hueco.top), Offset(hueco.right, hueco.bottom), 1.2f * u)
        // El pie: un borde más ancho, y el sombrerete cónico con su chimenea, con la luz del lado de la luz.
        borde(Rect(cuerpo.left - an * 0.08f, cuerpo.bottom - al * 0.04f, cuerpo.right + an * 0.08f, cuerpo.bottom + al * 0.06f))
        val tapa = poligonoRuinas(Offset(cuerpo.left - an * 0.1f, cuerpo.top), Offset(c.x - an * 0.12f, tope + al * 0.1f), Offset(c.x + an * 0.12f, tope + al * 0.1f), Offset(cuerpo.right + an * 0.1f, cuerpo.top))
        drawPath(tapa, Brush.horizontalGradient(listOf(mezcla(ci, Color.White, 0.15f), ci, mezcla(ci, hondo, 0.6f)), cuerpo.left, cuerpo.right))
        borde(Rect(c.x - an * 0.12f, tope, c.x + an * 0.12f, tope + al * 0.12f))
        drawRect(l.c(P.HIERRO_LUZ), Offset(cuerpo.left - an * 0.1f, cuerpo.top - 0.8f * u), Size(an * 1.2f, 1.6f * u))
        if (arde > 0f) drawRect(P.LLAMA.copy(alpha = 0.16f * arde), cuerpo.topLeft, cuerpo.size)
        if (l.k > 0.1f) drawLine(l.filo(0.7f), Offset(cuerpo.left + 0.8f * u, cuerpo.top), Offset(cuerpo.left + 0.8f * u, cuerpo.bottom), 1f * u)
    }
}
