package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La librería de caoba del estante de Pirata ([MuebleEstantePirata]): la cornisa volada con su cordón tallado, el friso y la
 * galería de latón; los montantes con su tablero hundido, el cabo tallado y los cáncamos de hierro con que va trincada; cada
 * casillero pintado de minio por dentro, con la tabla de encima vista por debajo, sus caras de dentro y sus sombras; las tablas
 * con su bocel y su placa de latón; la barandilla de mar de cada balda; y el cuerpo de dos cajones con tiradores de gota.
 */
internal object ObraEstantePirata {
    private val P = PaletaPirata

    /** Hacia dónde mira la luz en el cuerpo: de día, de la ventana (izquierda); de noche, del candelero (derecha). */
    private fun luzIzq(a: AirePirata) = !a.oscuro

    // ---------------------------------------------------------------- la cornisa (en la imagen de arriba)

    /**
     * La cornisa volada, de arriba abajo: el canto de la tapa, la corona lisa con su luz, el talón, el cordón tallado (la
     * moldura de cabo torcido), el friso con sus tableros entre los dados de los montantes y el bocel de abajo. De noche, por
     * tramos, con la luz del candelero.
     */
    fun DrawScope.cornisa(r: Rect, a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = r.height
        fun y(f: Float) = r.top + alto * f
        val tramos = if (a.lumbre) 12 else 1
        for (t in 0 until tramos) {
            val x0 = w * t / tramos; val x1 = w * (t + 1) / tramos + 1f
            val k = cerca(Offset((x0 + x1) / 2, r.center.y))
            fun banda(f0: Float, f1: Float, c: Color) = drawRect(c, Offset(x0, y(f0)), Size(x1 - x0, y(f1) - y(f0) + 0.6f))
            banda(0f, 0.07f, a.dia(P.CAOBA_LUZ, 0.22f, k))
            if (a.lumbre) banda(0f, 0.035f, a.filo(0.75f * k))
            banda(0.07f, 0.34f, a.luz(P.CAOBA, k))
            banda(0.1f, 0.15f, a.dia(P.CAOBA_LUZ, 0.05f, k))
            banda(0.34f, 0.41f, a.dia(P.CAOBA_LUZ, 0f, k))
            banda(0.41f, 0.49f, a.luz(P.CAOBA_OSC, k))
            banda(0.49f, 0.62f, a.luz(P.CAOBA_OSC, k))
            banda(0.62f, 0.9f, a.luz(P.CAOBA, k))
            banda(0.9f, 0.95f, a.dia(P.CAOBA_LUZ, 0.1f, k))
            banda(0.95f, 1f, a.luz(P.CAOBA_HONDA, k))
        }
        // La veta de la corona, una sola pasada.
        val veta = Path()
        for (j in 0 until 6) {
            val yy = y(0.1f + 0.035f * j)
            veta.moveTo(0f, yy); veta.cubicTo(w * 0.3f, yy + 1.5f * u * (azar(401, j) - 0.5f), w * 0.6f, yy - 1.2f * u, w, yy + 0.8f * u)
        }
        drawPath(veta, a.sombra(0.14f), style = Stroke(0.7f * u))
        // El cordón: el cabo torcido tallado, cada cordón con su luz y su sombra.
        val c0 = y(0.49f); val c1 = y(0.62f)
        val paso = (c1 - c0) * 0.9f
        val torcido = Path(); val brillo = Path()
        var x = -paso
        while (x < w + paso) {
            torcido.moveTo(x, c1); torcido.lineTo(x + paso, c0)
            brillo.moveTo(x + paso * 0.18f, c1 - (c1 - c0) * 0.22f); brillo.lineTo(x + paso * 0.72f, c0 + (c1 - c0) * 0.3f)
            x += paso
        }
        drawPath(torcido, a.sombra(0.55f), style = Stroke(1.3f * u))
        drawPath(brillo, if (a.oscuro) P.LLAMA.copy(alpha = 0.35f) else a.claro(0.42f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // Los tableros hundidos del friso, entre los dados que siguen a los montantes y uno en medio.
        val f0 = y(0.66f); val f1 = y(0.87f)
        val lado = w * MuebleEstantePirata.LADO
        val dados = listOf(0f to lado, w * 0.5f - lado * 0.35f to w * 0.5f + lado * 0.35f, w - lado to w)
        for (i in 0 until dados.size - 1) {
            val t = Rect(dados[i].second + w * 0.018f, f0, dados[i + 1].first - w * 0.018f, f1)
            val k = cerca(t.center)
            drawRect(a.luz(P.CAOBA_OSC, k), t.topLeft, t.size)
            drawRect(a.sombra(0.35f), t.topLeft, Size(t.width, 2f * u))
            drawRect(a.sombra(0.25f), t.topLeft, Size(2f * u, t.height))
            drawRect(a.dia(P.CAOBA_LUZ, 0f, k).copy(alpha = 0.7f), Offset(t.left, t.bottom - 1.2f * u), Size(t.width, 1.2f * u))
            drawRect(a.dia(P.CAOBA_LUZ, 0f, k).copy(alpha = 0.5f), Offset(t.right - 1.2f * u, t.top), Size(1.2f * u, t.height))
            // Un filete de boj en el tablero.
            drawRect(a.luz(Color(0xFFD9B97A), k).copy(alpha = 0.6f), t.deflate(4f * u).topLeft, t.deflate(4f * u).size, style = Stroke(0.7f * u))
        }
        dados.forEachIndexed { i, (d0, d1) ->
            val k = cerca(Offset((d0 + d1) / 2, (f0 + f1) / 2))
            val d = Rect(d0, y(0.62f), d1, y(0.9f))
            drawRect(a.luz(P.CAOBA, k), d.topLeft, d.size)
            drawRect(a.dia(P.CAOBA_LUZ, 0.05f, k), d.topLeft, Size(if (luzIzq(a)) 2f * u else d.width, 1.5f * u))
            drawRect(if (luzIzq(a)) a.claro(0.25f) else a.filo(0.3f * k), Offset(if (luzIzq(a)) d.left else d.right - 1.5f * u, d.top), Size(1.5f * u, d.height))
            // En el dado, una roseta de latón.
            if (i == 1) {
                val o = d.center
                drawCircle(a.sombra(0.35f), w * 0.012f, o + Offset(1f * u, 1.2f * u))
                drawCircle(a.luz(P.LATON_OSC, k), w * 0.012f, o)
                drawCircle(a.luz(P.LATON, k), w * 0.009f, o)
                destelloPirata(o - Offset(w * 0.004f, w * 0.004f), 1.2f * u, a, k)
            }
        }
        // La sombra de la cornisa sobre lo que tiene debajo y el canto de luz de la tapa.
        drawLine(a.claro(0.5f), Offset(0f, r.top + 0.6f * u), Offset(w, r.top + 0.6f * u), 1.1f * u)
    }

    /** La galería de latón de encima de la cornisa (va delante de lo que hay arriba): el pasamanos y sus balaustres. */
    fun DrawScope.galeria(a: AirePirata, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = w * 0.026f
        val y0 = apoyo - alto
        val n = 22
        val balaustres = Path(); val luces = Path()
        for (i in 0..n) {
            val x = w * 0.012f + (w * 0.976f) * i / n
            balaustres.moveTo(x, y0); balaustres.lineTo(x, apoyo)
            luces.moveTo(x - 0.5f * u, y0 + 2f * u); luces.lineTo(x - 0.5f * u, apoyo - 1f * u)
        }
        drawPath(balaustres, a.sombra(0.3f), style = Stroke(2.6f * u))
        drawPath(balaustres, a.luz(P.LATON_OSC, if (a.lumbre) 0.6f else 1f), style = Stroke(1.8f * u))
        drawPath(luces, if (a.oscuro) P.LLAMA.copy(alpha = 0.3f) else a.claro(0.4f), style = Stroke(0.6f * u))
        val tramos = if (a.lumbre) 8 else 1
        for (t in 0 until tramos) {
            val x0 = w * 0.008f + w * 0.984f * t / tramos; val x1 = w * 0.008f + w * 0.984f * (t + 1) / tramos + 0.5f
            val k = cerca(Offset((x0 + x1) / 2, y0))
            drawRect(a.sombra(0.3f), Offset(x0, y0 + 2.6f * u), Size(x1 - x0, 1.6f * u))
            drawRect(a.luz(P.LATON, k), Offset(x0, y0), Size(x1 - x0, 2.6f * u))
            drawRect(a.brillo(0.5f, k), Offset(x0, y0 + 0.4f * u), Size(x1 - x0, 0.8f * u))
        }
        listOf(w * 0.012f, w * 0.988f).forEach { x -> remachePirata(Offset(x, y0 + 1.2f * u), 2.2f * u, a, cerca(Offset(x, y0)), P.LATON) }
    }

    // ---------------------------------------------------------------- el cuerpo (en cada dibujo)

    /** Los caminos fijos de un casillero (las pinceladas del minio y sus roces), armados una vez por tamaño. */
    private var claveCasillero = ""
    private var pinceladas = Path()
    private var roces = Path()
    private var juntas = Path()
    private var lucesJuntas = Path()

    private fun caminosCasillero(ancho: Float, alto: Float, u: Float) {
        val clave = "$ancho:$alto:$u"
        if (clave == claveCasillero) return
        pinceladas = Path().apply {
            for (j in 0 until 16) {
                val x = ancho * azar(421, j); val y0 = alto * (0.1f + 0.3f * azar(422, j)); val y1 = alto * (0.6f + 0.4f * azar(423, j))
                moveTo(x, y0); cubicTo(x + 2f * u, y0 + (y1 - y0) * 0.3f, x - 2f * u, y0 + (y1 - y0) * 0.7f, x + 1f * u, y1)
            }
        }
        roces = Path().apply {
            for (j in 0 until 6) {
                val x = ancho * azar(424, j); val y = alto * (0.55f + 0.4f * azar(425, j))
                moveTo(x, y); lineTo(x + ancho * (0.03f + 0.06f * azar(426, j)), y + (azar(427, j) - 0.5f) * 3f * u)
            }
        }
        juntas = Path().apply { listOf(0.24f, 0.5f, 0.77f).forEach { f -> moveTo(ancho * f, 0f); lineTo(ancho * f, alto) } }
        lucesJuntas = Path().apply { listOf(0.24f, 0.5f, 0.77f).forEach { f -> moveTo(ancho * f + 1.3f * u, 0f); lineTo(ancho * f + 1.3f * u, alto) } }
        claveCasillero = clave
    }

    /**
     * Un casillero con su hondura: el fondo de minio con sus pinceladas, la tabla de encima vista por debajo, las dos caras de
     * dentro (la que mira a la luz, encendida) y la sombra de la tabla de arriba y del costado en el fondo. Se pinta un píxel de
     * más alrededor (bajo los montantes y las tablas, que van encima) para que ninguna junta deje ver el fondo de la pantalla.
     */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x0 = d.izquierda.right; val x1 = d.derecha.left
        val dT = w * 0.017f; val dI = w * 0.024f
        val izq = luzIzq(a)
        drawRect(P.MINIO_OSC, Offset(x0 - 1f, r.top - 1f), Size(x1 - x0 + 2f, r.height + 2f))
        // El fondo de minio y sus pinceladas.
        drawRect(a.dia(P.MINIO, 0f), Offset(x0 + dI, r.top + dT), Size(x1 - x0 - 2 * dI, r.height - dT))
        caminosCasillero(x1 - x0, r.height, u)
        translate(x0, r.top) {
            drawPath(pinceladas, a.dia(P.MINIO_OSC, 0f).copy(alpha = 0.13f), style = Stroke(4f * u))
            drawPath(roces, a.luz(P.CAOBA, 1f).copy(alpha = 0.3f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            drawPath(juntas, a.luz(P.MINIO_OSC, 1f).copy(alpha = 0.8f), style = Stroke(1.2f * u))
            drawPath(lucesJuntas, a.claro(0.12f), style = Stroke(0.8f * u))
        }
        // La sombra de la tabla de arriba y la del costado que tapa la luz.
        drawRect(a.sombra(0.42f), Offset(x0, r.top + dT), Size(x1 - x0, r.height * 0.07f))
        drawRect(a.sombra(0.22f), Offset(x0, r.top + dT + r.height * 0.07f), Size(x1 - x0, r.height * 0.12f))
        drawRect(a.sombra(0.1f), Offset(x0, r.top + dT + r.height * 0.19f), Size(x1 - x0, r.height * 0.14f))
        if (izq) drawRect(a.sombra(0.2f), Offset(x0 + dI, r.top + dT), Size(w * 0.02f, r.height - dT))
        else drawRect(a.sombra(0.2f), Offset(x1 - dI - w * 0.02f, r.top + dT), Size(w * 0.02f, r.height - dT))
        // La tabla de encima vista por debajo y las dos caras de dentro.
        drawPath(poligonoPirata(x0, r.top, x1, r.top, x1 - dI, r.top + dT, x0 + dI, r.top + dT), a.luz(P.CAOBA_OSC, 0.5f))
        val caraIzq = poligonoPirata(x0, r.top, x0 + dI, r.top + dT, x0 + dI, r.bottom, x0, r.bottom)
        val caraDer = poligonoPirata(x1, r.top, x1, r.bottom, x1 - dI, r.bottom, x1 - dI, r.top + dT)
        drawPath(caraIzq, if (izq) a.dia(P.MINIO_OSC, -0.1f) else a.dia(P.MINIO, 0.12f))
        drawPath(caraDer, if (izq) a.dia(P.MINIO, 0.16f) else a.dia(P.MINIO_OSC, -0.1f))
        // El canto de la esquina de dentro.
        drawLine(a.sombra(0.4f), Offset(x0 + dI, r.top + dT), Offset(x0 + dI, r.bottom), 1f * u)
        drawLine(a.sombra(0.3f), Offset(x1 - dI, r.top + dT), Offset(x1 - dI, r.bottom), 1f * u)
        drawLine(a.sombra(0.5f), Offset(x0 + dI, r.top + dT), Offset(x1 - dI, r.top + dT), 1f * u)
    }

    /**
     * Una tabla con su canto: el bocel de arriba (luz arriba, sombra abajo), la cara de caoba con su veta, el filete de abajo y
     * la sombra que deja en el casillero de debajo.
     */
    fun DrawScope.tabla(k: Int, r: Rect, a: AirePirata, cerca: (Offset) -> Float) {
        val u = density
        val h = r.height
        drawRect(a.luz(P.CAOBA, 1f), r.topLeft, r.size)
        drawRect(a.dia(P.CAOBA_LUZ, 0.18f), r.topLeft, Size(r.width, h * 0.09f))
        drawRect(a.dia(P.CAOBA_LUZ, 0.02f), Offset(r.left, r.top + h * 0.09f), Size(r.width, h * 0.12f))
        drawRect(a.luz(P.CAOBA_OSC, 1f), Offset(r.left, r.top + h * 0.21f), Size(r.width, h * 0.1f))
        drawRect(a.dia(P.CAOBA_LUZ, -0.05f).copy(alpha = 0.5f), Offset(r.left, r.top + h * 0.33f), Size(r.width, 1f * u))
        // La veta de la cara, de la tabla entera.
        val veta = Path()
        for (j in 0 until 4) {
            val y = r.top + h * (0.42f + 0.12f * j)
            veta.moveTo(r.left, y); veta.cubicTo(r.left + r.width * 0.35f, y + 1.6f * u * (azar(431 + k, j) - 0.5f), r.left + r.width * 0.65f, y - 1.2f * u, r.right, y + 0.8f * u)
        }
        drawPath(veta, a.sombra(0.18f), style = Stroke(0.7f * u))
        drawRect(a.luz(P.CAOBA_HONDA, 1f), Offset(r.left, r.bottom - h * 0.08f), Size(r.width, h * 0.08f))
        drawRect(a.dia(P.CAOBA_LUZ, 0f).copy(alpha = 0.6f), Offset(r.left, r.bottom - h * 0.1f), Size(r.width, 0.8f * u))
    }

    /**
     * Un montante: la cara de caoba con su tablero hundido y el cabo tallado a lo largo, el bocel del canto de dentro, el
     * capitel bajo la cornisa, la basa sobre los cajones y dos cáncamos de hierro (la librería va trincada al costado).
     */
    fun DrawScope.montante(r: Rect, izquierdo: Boolean, a: AirePirata, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        if (r.height < 4f) return
        val izq = luzIzq(a)
        drawRect(a.luz(P.CAOBA, 1f), r.topLeft, r.size)
        // El canto que mira a la luz y el que no.
        val luzX = if (izq) r.left else r.right - 2f * u
        drawRect(a.dia(P.CAOBA_LUZ, 0.15f), Offset(luzX, r.top), Size(2f * u, r.height))
        drawRect(a.sombra(0.3f), Offset(if (izq) r.right - 2.2f * u else r.left, r.top), Size(2.2f * u, r.height))
        // El tablero hundido.
        val t = Rect(r.left + r.width * 0.2f, r.top + w * 0.03f, r.right - r.width * 0.2f, r.bottom - w * 0.03f)
        if (t.height > 4f) {
            drawRect(a.luz(P.CAOBA_OSC, 1f), t.topLeft, t.size)
            drawRect(a.sombra(0.4f), t.topLeft, Size(t.width, 2.4f * u))
            drawRect(a.sombra(0.3f), if (izq) t.topLeft else Offset(t.right - 2f * u, t.top), Size(2f * u, t.height))
            drawRect(a.dia(P.CAOBA_LUZ, 0.05f).copy(alpha = 0.7f), if (izq) Offset(t.right - 1.3f * u, t.top) else t.topLeft, Size(1.3f * u, t.height))
            // El cabo tallado: el cuerpo redondo y los cordones en diagonal.
            val cx = t.center.x; val g = t.width * 0.62f
            drawRect(a.luz(P.CAOBA, 1f), Offset(cx - g / 2, t.top + 3f * u), Size(g, t.height - 6f * u))
            drawRect(a.dia(P.CAOBA_LUZ, 0.12f).copy(alpha = 0.6f), Offset(if (izq) cx - g / 2 else cx + g * 0.12f, t.top + 3f * u), Size(g * 0.38f, t.height - 6f * u))
            val paso = g * 0.8f
            val torcido = Path(); val brillo = Path()
            var y = t.top + 3f * u
            while (y < t.bottom - 3f * u - paso) {
                torcido.moveTo(cx - g / 2, y + paso); torcido.lineTo(cx + g / 2, y)
                brillo.moveTo(cx - g * 0.3f, y + paso * 0.7f); brillo.lineTo(cx + g * 0.05f, y + paso * 0.32f)
                y += paso
            }
            drawPath(torcido, a.sombra(0.5f), style = Stroke(1.2f * u))
            drawPath(brillo, if (a.oscuro) P.LLAMA.copy(alpha = 0.4f) else a.claro(0.4f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        // El bocel del canto de dentro.
        val bx = if (izquierdo) r.right - r.width * 0.12f else r.left
        drawRect(a.luz(P.CAOBA_OSC, 1f), Offset(bx, r.top), Size(r.width * 0.12f, r.height))
        drawRect(a.dia(P.CAOBA_LUZ, 0.1f).copy(alpha = 0.7f), Offset(bx + r.width * 0.03f, r.top), Size(r.width * 0.03f, r.height))
        // El capitel bajo la cornisa y la basa.
        drawRect(a.luz(P.CAOBA, 1f), Offset(r.left, r.top), Size(r.width, w * 0.02f))
        drawRect(a.dia(P.CAOBA_LUZ, 0.1f), Offset(r.left, r.top + w * 0.02f - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, r.top + w * 0.02f), Size(r.width, 1.6f * u))
        drawRect(a.luz(P.CAOBA, 1f), Offset(r.left, r.bottom - w * 0.02f), Size(r.width, w * 0.02f))
        drawRect(a.dia(P.CAOBA_LUZ, 0.12f), Offset(r.left, r.bottom - w * 0.02f), Size(r.width, 1.4f * u))
        // Los cáncamos: un perno de ojo con su argolla.
        listOf(r.top + w * 0.06f, r.bottom - w * 0.07f).forEach { y ->
            if (y > r.top + w * 0.03f && y < r.bottom - w * 0.03f) {
                val o = Offset(r.center.x, y)
                remachePirata(o, 2.4f * u, a, 1f)
                drawArc(a.sombra(0.4f), 20f, 140f, false, Offset(o.x - 4.2f * u, o.y - 1f * u), Size(8.4f * u, 9f * u), style = Stroke(1.6f * u))
                drawArc(a.luz(P.HIERRO, 1f), 10f, 160f, false, Offset(o.x - 4.6f * u, o.y - 2.4f * u), Size(9.2f * u, 9.4f * u), style = Stroke(1.5f * u))
                drawArc(a.brillo(0.4f, 1f), 120f, 40f, false, Offset(o.x - 4.6f * u, o.y - 2.4f * u), Size(9.2f * u, 9.4f * u), style = Stroke(0.6f * u))
            }
        }
    }

    /** La barandilla de mar de una balda (lo que se ve entre los volúmenes y en los huecos): la varilla y sus dos soportes. */
    fun DrawScope.barandilla(r: Rect, d: DisposicionEstante, a: AirePirata, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val dI = w * 0.024f
        val x0 = d.izquierda.right + dI * 0.4f; val x1 = d.derecha.left - dI * 0.4f
        val y = r.bottom - MuebleEstantePirata.RIEL * u
        varillaPirata(x0, x1, y, a, 1f)
        listOf(x0, x1).forEach { x ->
            val s = Rect(x - 2.6f * u, y - 3.5f * u, x + 2.6f * u, y + MuebleEstantePirata.RIEL_G * u + 3.5f * u)
            drawRoundRect(a.luz(P.LATON_OSC, 1f), s.topLeft, s.size, CornerRadius(1.4f * u))
            drawRoundRect(a.luz(P.LATON, 1f), s.topLeft, Size(s.width * 0.6f, s.height), CornerRadius(1.4f * u))
            drawCircle(a.luz(P.LATON_OSC, 1f), 0.8f * u, Offset(s.center.x, s.top + 1.5f * u))
            drawCircle(a.luz(P.LATON_OSC, 1f), 0.8f * u, Offset(s.center.x, s.bottom - 1.5f * u))
        }
    }

    /** El rótulo del mes: una tarjeta en su portaetiquetas de latón atornillado al canto de la tabla. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AirePirata, k: Float) {
        val u = density
        val r = p.r
        drawRoundRect(a.sombra(0.4f), Offset(r.left + 1.4f * u, r.top + 1.6f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(P.LATON_OSC, maxOf(k, 0.45f)), r.topLeft, r.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(P.LATON, maxOf(k, 0.45f)), r.topLeft, Size(r.width, r.height * 0.55f), CornerRadius(2f * u))
        drawRect(a.brillo(0.55f, maxOf(k, 0.4f)), Offset(r.left + 3f * u, r.top + 1f * u), Size(r.width - 6f * u, 0.8f * u))
        val tarjeta = Rect(r.left + 7f * u, r.top + 2.6f * u, r.right - 7f * u, r.bottom - 2.6f * u)
        drawRect(a.etiqueta(P.PAPEL, k), tarjeta.topLeft, tarjeta.size)
        drawRect(a.sombra(0.25f), tarjeta.topLeft, Size(tarjeta.width, 1.2f * u))
        listOf(r.left + 3.6f * u, r.right - 3.6f * u).forEach { x ->
            val o = Offset(x, r.center.y)
            drawCircle(a.luz(P.LATON_OSC, k), 1.6f * u, o)
            drawLine(a.sombra(0.6f), Offset(x - 1.1f * u, o.y), Offset(x + 1.1f * u, o.y), 0.6f * u)
        }
        textoEn(medidor, p.texto, tarjeta.deflate(1.5f * u), PaletaPirata.LETRA, P.TINTA, 11f, peso = FontWeight.SemiBold)
    }

    // ---------------------------------------------------------------- la luz de la noche sobre el cuerpo

    private var claveMapa = ""
    private var mapa: ImageBitmap? = null

    /**
     * La luz sobre el cuerpo, en una imagen chica (un píxel por cada 1/36 del ancho) pintada una vez por tamaño y modo y estirada
     * en un solo dibujo encima de todo el cuerpo: de noche, lo que se apaga con la distancia al candelero y al fanal (hacia el
     * azul de mar hondo, con el calor de la llama cerca); de día, lo que oscurece la sombra hacia abajo y hacia la derecha.
     */
    fun DrawScope.luzCuerpo(arriba: Float, abajo: Float, a: AirePirata, yFanal: Float, velas: FloatArray) {
        val w = size.width
        val y0 = ceil(arriba).toInt(); val y1 = floor(abajo).toInt()
        if (y1 <= y0 || w < 2f) return
        val cols = 36
        val filas = ((y1 - y0) / (w / cols)).toInt().coerceIn(2, 400)
        val clave = "${w.toInt()}:$y0:$y1:${a.oscuro}:${a.noche}:$yFanal:${velas.joinToString(",")}"
        val img = mapa.takeIf { clave == claveMapa } ?: ImageBitmap(cols, filas).also { nueva ->
            CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(nueva), Size(cols.toFloat(), filas.toFloat())) {
                for (j in 0 until filas) for (i in 0 until cols) {
                    val x = (i + 0.5f) / cols
                    val y = (y0 + (j + 0.5f) / filas * (y1 - y0)) / w
                    drawRect(mascara(a, x, y, yFanal, velas), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f))
                }
            }
            mapa = nueva; claveMapa = clave
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, y0), IntSize(ceil(w).toInt(), y1 - y0))
    }

    /** El color (con su alfa) que se pone encima del cuerpo en ([x], [y]), en anchos. */
    private fun mascara(a: AirePirata, x: Float, y: Float, yFanal: Float, velas: FloatArray): Color {
        if (!a.lumbre) {
            // De día: algo más hondo abajo y a la derecha, lejos de la ventana.
            val f = ((y - 0.6f) * 0.08f + (x - 0.2f).coerceAtLeast(0f) * 0.08f).coerceIn(0f, 0.16f)
            return PaletaPirata.FRIO.copy(alpha = f)
        }
        val k = MuebleEstantePirata.calor(x, y, yFanal, a.noche, velas)
        if (!a.oscuro) {
            // El ocaso de los Sueños: la luz rasante viene de la ventana (arriba a la izquierda); lejos de ella, la sombra
            // violeta crece hacia abajo y hacia la derecha; la vela calienta lo que tiene cerca.
            val violeta = (0.06f + 0.16f * ((x - 0.15f) * 0.9f + (y - 0.6f) * 0.3f).coerceIn(0f, 1f)) * (1f - k)
            val calido = 0.2f * k
            val al = (violeta + calido).coerceIn(0f, 1f)
            return mezcla(PaletaPirata.SOMBRA_OCASO, PaletaPirata.LLAMA, if (al > 0.001f) calido / al else 0f).copy(alpha = al)
        }
        val hondo = (1f - k) * 0.86f
        val calido = 0.18f * k
        val al = (hondo + calido).coerceIn(0f, 1f)
        val f = if (al > 0.001f) calido / al else 0f
        return mezcla(a.hondo, PaletaPirata.BRASA, f).copy(alpha = al)
    }

    // ---------------------------------------------------------------- el cuerpo de los cajones (en la imagen de abajo)

    /**
     * El cuerpo de dos cajones bajo la última tabla: la moldura de arriba, los dos frentes de caoba con su ceja, su tirador de
     * gota de latón con su roseta y su bocallave, las basas de los montantes y el zócalo sobre la cubierta.
     */
    fun DrawScope.cajonera(r: Rect, a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val h = r.height
        val lado = w * MuebleEstantePirata.LADO
        val k = cerca(r.center)
        drawRect(a.luz(P.CAOBA, k), r.topLeft, r.size)
        // La moldura de arriba.
        drawRect(a.dia(P.CAOBA_LUZ, 0.15f, k), r.topLeft, Size(w, h * 0.06f))
        drawRect(a.luz(P.CAOBA_OSC, k), Offset(0f, r.top + h * 0.06f), Size(w, h * 0.06f))
        // Los frentes de los cajones.
        val y0 = r.top + h * 0.17f; val y1 = r.bottom - h * 0.2f
        val m = w * 0.5f
        listOf(lado + w * 0.012f to m - w * 0.008f, m + w * 0.008f to w - lado - w * 0.012f).forEachIndexed { i, (x0, x1) ->
            val f = Rect(x0, y0, x1, y1)
            val kf = cerca(f.center)
            drawRect(a.sombra(0.5f), f.inflate(1.6f * u).topLeft, f.inflate(1.6f * u).size)
            drawRect(a.luz(P.CAOBA, kf), f.topLeft, f.size)
            val veta = Path()
            for (j in 0 until 5) {
                val y = f.top + f.height * (0.15f + 0.17f * j)
                veta.moveTo(f.left, y); veta.cubicTo(f.left + f.width * 0.3f, y + 2f * u * (azar(441 + i, j) - 0.5f), f.left + f.width * 0.7f, y - 1.5f * u, f.right, y + 1f * u)
            }
            drawPath(veta, a.sombra(0.16f), style = Stroke(0.8f * u))
            // La ceja del cajón.
            drawRect(a.dia(P.CAOBA_LUZ, 0.2f, kf), f.topLeft, Size(f.width, 1.6f * u))
            drawRect(a.dia(P.CAOBA_LUZ, 0.1f, kf), f.topLeft, Size(1.6f * u, f.height))
            drawRect(a.sombra(0.4f), Offset(f.left, f.bottom - 1.6f * u), Size(f.width, 1.6f * u))
            // La bocallave y el tirador de gota.
            val c = f.center
            val bl = Rect(c.x - w * 0.012f, f.top + f.height * 0.16f, c.x + w * 0.012f, f.top + f.height * 0.42f)
            drawOval(a.luz(P.LATON, kf), bl.topLeft, bl.size)
            drawOval(a.sombra(0.3f), bl.topLeft, bl.size, style = Stroke(0.8f * u))
            drawCircle(a.luz(P.CAOBA_HONDA, kf), w * 0.0035f, Offset(c.x, bl.center.y - w * 0.002f))
            drawLine(a.luz(P.CAOBA_HONDA, kf), Offset(c.x, bl.center.y), Offset(c.x, bl.center.y + w * 0.007f), 1.4f * u)
            val ro = Offset(c.x, f.top + f.height * 0.6f)
            drawCircle(a.sombra(0.35f), w * 0.013f, ro + Offset(1f * u, 1.4f * u))
            drawCircle(a.luz(P.LATON_OSC, kf), w * 0.013f, ro)
            drawCircle(a.luz(P.LATON, kf), w * 0.01f, ro)
            val gota = Path().apply {
                moveTo(ro.x, ro.y + w * 0.004f)
                cubicTo(ro.x + w * 0.009f, ro.y + w * 0.018f, ro.x + w * 0.007f, ro.y + w * 0.03f, ro.x, ro.y + w * 0.031f)
                cubicTo(ro.x - w * 0.007f, ro.y + w * 0.03f, ro.x - w * 0.009f, ro.y + w * 0.018f, ro.x, ro.y + w * 0.004f)
                close()
            }
            drawPath(gota, a.sombra(0.35f), alpha = 1f)
            translate(-1.2f * u, -1f * u) { drawPath(gota, a.luz(P.LATON, kf)) }
            drawCircle(a.brillo(0.6f, kf), w * 0.0025f, Offset(ro.x - w * 0.003f, ro.y + w * 0.018f))
            destelloPirata(Offset(ro.x - w * 0.004f, ro.y - w * 0.003f), 1.1f * u, a, kf)
        }
        // Las basas de los montantes, hasta el suelo.
        listOf(0f to lado, w - lado to w).forEach { (x0, x1) ->
            drawRect(a.luz(P.CAOBA, k), Offset(x0, r.top), Size(x1 - x0, h))
            drawRect(a.dia(P.CAOBA_LUZ, 0.12f, k), Offset(x0, r.top), Size(2f * u, h))
            drawRect(a.sombra(0.3f), Offset(x1 - 2f * u, r.top), Size(2f * u, h))
        }
        // El zócalo sobre la cubierta.
        val z = r.bottom - h * 0.16f
        drawRect(a.luz(P.CAOBA_OSC, k), Offset(0f, z), Size(w, r.bottom - z))
        drawRect(a.dia(P.CAOBA_LUZ, 0.1f, k), Offset(0f, z), Size(w, 1.4f * u))
        drawRect(a.luz(P.CAOBA_HONDA, k), Offset(0f, r.bottom - 1.6f * u), Size(w, 1.6f * u + 1f))
    }
}

/**
 * La varilla de latón de la barandilla de mar, de [x0] a [x1] con su canto de arriba en [y]: la sombra de debajo, el cuerpo,
 * el brillo y el filo de abajo. La pintan igual el mueble (en los huecos) y cada entrada (por delante de ella).
 */
internal fun DrawScope.varillaPirata(x0: Float, x1: Float, y: Float, a: AirePirata, k: Float) {
    val u = density
    val g = MuebleEstantePirata.RIEL_G * u
    val p = PaletaPirata
    drawRect(a.sombra(0.35f), Offset(x0, y + g), Size(x1 - x0, 1.6f * u))
    drawRect(a.luz(p.LATON_OSC, k), Offset(x0, y), Size(x1 - x0, g))
    drawRect(a.luz(p.LATON, k), Offset(x0, y), Size(x1 - x0, g * 0.6f))
    drawRect(a.brillo(0.7f, maxOf(k, 0.3f)), Offset(x0, y + g * 0.18f), Size(x1 - x0, g * 0.2f))
}
