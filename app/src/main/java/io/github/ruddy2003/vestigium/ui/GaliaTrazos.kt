package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los trazos que se repiten en las figuras de Galia (`docs/diseno/temas/galia.md`): sus colores de dibujo y las figuras
 * que comparten las superficies: el torques de oro, la hoguera, la hoja de roble, el caballo de las monedas con su rueda,
 * la «S» de las monedas, el muérdago, el carnyx y el emblema. La luz viene siempre de arriba a la izquierda.
 */
object TrazosGalia {
    /** El roble, a la luz y en sombra; el muérdago y sus bayas. */
    val ROBLE = Color(0xFF4E6B2E)
    val ROBLE_SOMBRA = Color(0xFF2E4219)
    val MUERDAGO = Color(0xFF9DB560)
    val BAYA = Color(0xFFF2EEDC)
    /** El barro y la paja de las casas. */
    val BARRO = Color(0xFFB89A62)
    val PAJA = Color(0xFFD8C48A)
    /** La madera de la empalizada. */
    val MADERA = Color(0xFF6B4B2A)
    /** El oro de los torques y de las monedas, y su brillo. */
    val ORO = Color(0xFFC9962B)
    val ORO_BRILLO = Color(0xFFF0D27A)
    /** El bronce del carnyx y del caldero, y su verdín. */
    val BRONCE = Color(0xFF9A6A3A)
    val VERDIN = Color(0xFF5F8C76)
    /** El hierro de las espadas y de la fragua. */
    val HIERRO = Color(0xFF5A5C5E)
    /** La lana a cuadros de los mantos. */
    val LANA_ROJA = Color(0xFF8E3B2A)
    val LANA_VERDE = Color(0xFF3F5E2A)
    val LANA_OCRE = Color(0xFFC9A24A)
    /** El humo y la niebla. */
    val HUMO = Color(0xFFB9B4A6)
    val NIEBLA = Color(0xFFDDE0D8)
    /** La luna y la hoguera, con su halo. */
    val LUNA = Color(0xFFE9E6D6)
    val FUEGO = Color(0xFFF2A040)
    val FUEGO_HALO = Color(0xFFFFD28A)
    /** Roma: solo en lo que es de Roma (el campamento, el cerco, lo que se ve a lo lejos). */
    val ROMA = Color(0xFFA33A2A)

    /** La brasa honda de la hoguera, debajo del naranja. */
    private val BRASA = Color(0xFFC4511F)

    /** Los puntos de una curva de Bézier cúbica (de [a] a [d], con los controles [b] y [c]), en [n] tramos. */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, n: Int): List<Offset> = List(n + 1) { i ->
        val t = i / n.toFloat(); val s = 1f - t
        Offset(s * s * s * a.x + 3 * s * s * t * b.x + 3 * s * t * t * c.x + t * t * t * d.x,
            s * s * s * a.y + 3 * s * s * t * b.y + 3 * s * t * t * c.y + t * t * t * d.y)
    }

    /** Una cinta de ancho variable a lo largo de [pts]: el ancho en cada punto lo da [ancho] (de 0 a 1 a lo largo). */
    private fun cinta(pts: List<Offset>, ancho: (Float) -> Float): Path {
        val n = pts.size
        val izq = ArrayList<Offset>(n); val der = ArrayList<Offset>(n)
        for (i in 0 until n) {
            val a = pts[max(i - 1, 0)]; val b = pts[min(i + 1, n - 1)]
            val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
            val w = ancho(i / (n - 1).coerceAtLeast(1).toFloat()) / 2
            izq.add(Offset(pts[i].x - dy / l * w, pts[i].y + dx / l * w)); der.add(Offset(pts[i].x + dy / l * w, pts[i].y - dx / l * w))
        }
        return Path().apply {
            moveTo(izq[0].x, izq[0].y); for (k in 1 until n) lineTo(izq[k].x, izq[k].y)
            for (k in n - 1 downTo 0) lineTo(der[k].x, der[k].y)
            close()
        }
    }

    private fun poligono(p: List<Offset>): Path = Path().apply {
        moveTo(p[0].x, p[0].y)
        for (k in 1 until p.size) lineTo(p[k].x, p[k].y)
        close()
    }

    /**
     * El torques: un aro de oro torcido como una cuerda, abierto por abajo, con sus dos remates de trompeta que acaban en
     * disco y se miran a través del hueco (el torques «de tampones» de la Edad del Hierro). [r] es el radio del aro y
     * [grueso] el del cuerpo. La torsión se marca con los surcos en diagonal y un brillo en cada vuelta, más vivo del lado
     * de la luz; debajo, su sombra corta.
     */
    fun DrawScope.torques(c: Offset, r: Float, grueso: Float, oro: Color = ORO, brillo: Color = ORO_BRILLO) {
        val g = grueso
        val hueco = 44f                            // los grados del hueco de abajo
        val inicio = 90f + hueco / 2
        val barrido = 360f - hueco
        val oscuro = lerp(oro, Color.Black, 0.42f)
        val esquina = Offset(c.x - r, c.y - r); val caja = Size(r * 2, r * 2)
        // La sombra que echa sobre lo de atrás.
        drawArc(Color.Black.copy(alpha = 0.16f), inicio, barrido, false, esquina + Offset(g * 0.25f, g * 0.45f), caja, style = Stroke(g * 1.05f, cap = StrokeCap.Round))
        // El cuerpo: el borde en sombra y el oro encima, un poco más fino.
        drawArc(oscuro, inicio, barrido, false, esquina, caja, style = Stroke(g, cap = StrokeCap.Round))
        drawArc(oro, inicio, barrido, false, esquina, caja, style = Stroke(g * 0.74f, cap = StrokeCap.Round))
        val lx = -0.6f; val ly = -0.8f                  // hacia la luz
        if (g >= 2.4f) {
            // La torsión: los surcos en diagonal y, entre surco y surco, el brillo de la vuelta.
            val paso = max(7f, Math.toDegrees((g * 0.95f / r).toDouble()).toFloat())
            var a = inicio + paso * 0.6f
            while (a < inicio + barrido - paso * 0.6f) {
                val ra = Math.toRadians(a.toDouble()); val rb = Math.toRadians((a + paso * 0.85f).toDouble())
                val dentro = Offset(c.x + (r - g * 0.42f) * cos(ra).toFloat(), c.y + (r - g * 0.42f) * sin(ra).toFloat())
                val fuera = Offset(c.x + (r + g * 0.42f) * cos(rb).toFloat(), c.y + (r + g * 0.42f) * sin(rb).toFloat())
                drawLine(oscuro.copy(alpha = 0.75f), dentro, fuera, g * 0.13f, StrokeCap.Round)
                val rm = Math.toRadians((a + paso * 0.5f).toDouble())
                val nx = cos(rm).toFloat(); val ny = sin(rm).toFloat()
                val luz = (0.5f + 0.5f * (nx * lx + ny * ly)).coerceIn(0f, 1f)
                val m = Offset(c.x + (r + g * 0.16f * (nx * lx + ny * ly)) * nx, c.y + (r + g * 0.16f * (nx * lx + ny * ly)) * ny)
                val d = Offset((fuera.x - dentro.x) * 0.22f, (fuera.y - dentro.y) * 0.22f)
                drawLine(brillo.copy(alpha = 0.25f + 0.6f * luz), m - d, m + d, g * 0.2f, StrokeCap.Round)
                a += paso
            }
        } else {
            drawArc(brillo.copy(alpha = 0.5f), inicio + 120f, 100f, false, esquina, caja, style = Stroke(g * 0.3f, cap = StrokeCap.Round))
        }
        // Los remates: el cuello, la trompeta que se abre y el disco de cara al hueco.
        for (lado in 0..1) {
            val ang = if (lado == 0) inicio else inicio + barrido
            val ra = Math.toRadians(ang.toDouble())
            val p = Offset(c.x + r * cos(ra).toFloat(), c.y + r * sin(ra).toFloat())
            // Hacia el hueco: contra el sentido del aro en el primer remate, a favor en el segundo.
            val s = if (lado == 0) -1f else 1f
            val tx = -sin(ra).toFloat() * s; val ty = cos(ra).toFloat() * s
            val nx = -ty; val ny = tx
            val largo = g * 1.15f
            fun q(f: Float, w: Float) = Offset(p.x + tx * largo * f + nx * w, p.y + ty * largo * f + ny * w)
            val trompeta = Path().apply {
                moveTo(q(-0.15f, g * 0.4f).x, q(-0.15f, g * 0.4f).y)
                quadraticTo(q(0.55f, g * 0.38f).x, q(0.55f, g * 0.38f).y, q(1f, g * 0.95f).x, q(1f, g * 0.95f).y)
                lineTo(q(1f, -g * 0.95f).x, q(1f, -g * 0.95f).y)
                quadraticTo(q(0.55f, -g * 0.38f).x, q(0.55f, -g * 0.38f).y, q(-0.15f, -g * 0.4f).x, q(-0.15f, -g * 0.4f).y)
                close()
            }
            translate(g * 0.2f, g * 0.35f) { drawPath(trompeta, Color.Black.copy(alpha = 0.14f)) }
            drawPath(trompeta, oro)
            drawPath(trompeta, oscuro, style = Stroke(max(g * 0.09f, 0.6f)))
            // Un collarín donde arranca la trompeta.
            drawLine(oscuro, q(0.08f, g * 0.46f), q(0.08f, -g * 0.46f), max(g * 0.14f, 0.7f))
            // El disco, casi de canto: un óvalo alto con su borde y el brillo arriba.
            val centroDisco = q(1.06f, 0f)
            val grados = Math.toDegrees(kotlin.math.atan2(ty, tx).toDouble()).toFloat()
            rotate(grados, centroDisco) {
                val ancho = g * 0.5f; val alto = g * 2.1f
                drawOval(oscuro, Offset(centroDisco.x - ancho / 2, centroDisco.y - alto / 2), Size(ancho, alto))
                drawOval(oro, Offset(centroDisco.x - ancho * 0.32f, centroDisco.y - alto * 0.44f), Size(ancho * 0.64f, alto * 0.88f))
            }
            if (g >= 2.4f) drawCircle(brillo.copy(alpha = 0.7f), g * 0.22f, q(0.95f, -g * 0.5f * (if (ny < 0) -1f else 1f)))
        }
    }

    /**
     * La hoguera: el corro de piedras, los troncos cruzados con su corte claro y la punta en brasa, el lecho de brasas y la
     * llama en tres capas (la brasa honda, el naranja y el corazón claro), con alguna chispa. [c] es el pie de la hoguera
     * y [w] el ancho de los troncos; [llama] (0 a 1) la hace más alta y la mece. El halo cálido alumbra alrededor.
     */
    fun DrawScope.hoguera(c: Offset, w: Float, llama: Float = 1f, madera: Color = MADERA) {
        val alto = w * 0.95f * (0.78f + 0.22f * llama)
        val mece = (llama - 0.5f) * w * 0.07f
        val foco = Offset(c.x, c.y - alto * 0.35f)
        drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.42f), FUEGO.copy(alpha = 0.12f), FUEGO_HALO.copy(alpha = 0f)), foco, w * 1.45f), w * 1.45f, foco)
        // El suelo alumbrado.
        drawOval(Brush.radialGradient(listOf(FUEGO.copy(alpha = 0.35f), FUEGO.copy(alpha = 0f)), c, w * 0.75f), Offset(c.x - w * 0.75f, c.y - w * 0.16f), Size(w * 1.5f, w * 0.32f))
        val piedra = Color(0xFF5E584E)
        // Las piedras de atrás.
        for (k in 0..2) {
            val a = PI * (1.2 + 0.3 * k)
            val p = Offset(c.x + w * 0.48f * cos(a).toFloat(), c.y + w * 0.1f * sin(a).toFloat() - w * 0.02f)
            drawOval(lerp(piedra, Color.Black, 0.25f), Offset(p.x - w * 0.08f, p.y - w * 0.05f), Size(w * 0.16f, w * 0.1f))
        }
        // Los troncos: dos en aspa y uno atrás, cada uno con su lado en sombra, el corte claro y la punta quemada.
        fun tronco(desde: Offset, hasta: Offset, g: Float) {
            drawLine(lerp(madera, Color.Black, 0.35f), desde, hasta, g, StrokeCap.Round)
            val dx = hasta.x - desde.x; val dy = hasta.y - desde.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
            val ox = dy / l * g * 0.18f; val oy = -dx / l * g * 0.18f
            drawLine(madera, Offset(desde.x + ox, desde.y - abs(oy)), Offset(hasta.x + ox, hasta.y - abs(oy)), g * 0.62f, StrokeCap.Round)
            drawLine(lerp(madera, Color.Black, 0.6f), Offset(desde.x + dx * 0.72f, desde.y + dy * 0.72f), hasta, g * 0.9f, StrokeCap.Round)
            drawCircle(BRASA.copy(alpha = 0.85f), g * 0.22f, Offset(desde.x + dx * 0.84f, desde.y + dy * 0.84f))
            drawCircle(lerp(madera, Color(0xFFE8C99A), 0.55f), g * 0.5f, desde)
            drawCircle(lerp(madera, Color.Black, 0.2f), g * 0.5f, desde, style = Stroke(g * 0.12f))
            drawCircle(lerp(madera, Color.Black, 0.25f), g * 0.12f, desde)
        }
        val g = w * 0.13f
        tronco(Offset(c.x + w * 0.05f, c.y - w * 0.12f), Offset(c.x - w * 0.02f, c.y - w * 0.05f), g * 0.9f)
        tronco(Offset(c.x - w * 0.5f, c.y + w * 0.02f), Offset(c.x + w * 0.16f, c.y - w * 0.15f), g)
        tronco(Offset(c.x + w * 0.5f, c.y + w * 0.02f), Offset(c.x - w * 0.16f, c.y - w * 0.15f), g)
        // El lecho de brasas.
        drawOval(Brush.radialGradient(listOf(FUEGO_HALO, BRASA.copy(alpha = 0.9f), BRASA.copy(alpha = 0f)), Offset(c.x, c.y - w * 0.08f), w * 0.3f),
            Offset(c.x - w * 0.3f, c.y - w * 0.16f), Size(w * 0.6f, w * 0.16f))
        // La llama: lenguas que salen de las brasas y se tuercen arriba.
        fun lengua(dx: Float, h: Float, ancho: Float, inclina: Float, color: Color) {
            val bx = c.x + dx; val by = c.y - w * 0.1f
            val px = bx + mece + inclina
            val p = Path().apply {
                moveTo(bx - ancho, by)
                cubicTo(bx - ancho * 1.25f, by - h * 0.42f, px - ancho * 0.5f, by - h * 0.7f, px, by - h)
                cubicTo(px + ancho * 0.12f, by - h * 0.62f, bx + ancho * 1.2f, by - h * 0.38f, bx + ancho, by)
                close()
            }
            drawPath(p, color)
        }
        lengua(-w * 0.17f, alto * 0.62f, w * 0.13f, -w * 0.08f, BRASA.copy(alpha = 0.9f))
        lengua(w * 0.18f, alto * 0.7f, w * 0.12f, w * 0.06f, BRASA.copy(alpha = 0.9f))
        lengua(0f, alto, w * 0.21f, w * 0.02f, BRASA)
        lengua(-w * 0.1f, alto * 0.55f, w * 0.12f, -w * 0.05f, FUEGO)
        lengua(w * 0.09f, alto * 0.6f, w * 0.11f, w * 0.04f, FUEGO)
        lengua(0f, alto * 0.86f, w * 0.15f, w * 0.01f, FUEGO)
        lengua(0f, alto * 0.48f, w * 0.09f, 0f, FUEGO_HALO)
        lengua(0f, alto * 0.22f, w * 0.05f, 0f, Color(0xFFFFF4DC))
        // Las piedras de delante, alumbradas por arriba.
        for (k in 0..3) {
            val a = PI * (0.12 + 0.25 * k)
            val p = Offset(c.x + w * 0.52f * cos(a).toFloat(), c.y + w * 0.08f * sin(a).toFloat() + w * 0.02f)
            val pw = w * (0.17f - 0.02f * (k % 2)); val ph = w * 0.11f
            drawOval(piedra, Offset(p.x - pw / 2, p.y - ph / 2), Size(pw, ph))
            drawOval(lerp(piedra, FUEGO, 0.45f), Offset(p.x - pw * 0.35f, p.y - ph * 0.48f), Size(pw * 0.7f, ph * 0.4f))
        }
        // Las chispas, que suben con la llama.
        for (k in 0..2) {
            val f = ((llama + k * 0.37f) % 1f)
            val p = Offset(c.x + (k - 1) * w * 0.14f + mece * 2, c.y - alto * (0.85f + 0.5f * f))
            drawCircle(FUEGO_HALO.copy(alpha = (1f - f) * 0.9f), w * 0.022f, p)
        }
    }

    /**
     * La hoja de roble (el roble albar de la Galia): ancha arriba, con cuatro pares de lóbulos redondos de senos hondos,
     * un poco desparejos de un lado al otro, las dos orejuelas en la base y el rabillo corto. [largo] es su largo y
     * [angulo], el giro en grados (0 = hacia arriba). Media hoja va en sombra y un nervio lleva a cada lóbulo.
     */
    fun DrawScope.hojaRoble(c: Offset, largo: Float, angulo: Float, color: Color = ROBLE, nervio: Color = ROBLE_SOMBRA) {
        rotate(angulo, c) {
            val l = largo
            val n = 4f                                  // pares de lóbulos
            // El medio ancho en [s] (0 en la base, 1 en la punta) de un lado: la silueta ancha arriba, los lóbulos y la orejuela.
            fun ancho(s: Float, fase: Float): Float {
                val envolvente = 0.31f * Math.pow(sin(PI * Math.pow(s.toDouble(), 0.78)).coerceAtLeast(0.0), 0.62).toFloat()
                val lobulo = (0.5f + 0.5f * cos(2 * PI.toFloat() * (n * s - 0.12f) + fase))
                val oreja = 0.07f * kotlin.math.exp(-((s - 0.04f) / 0.035f) * ((s - 0.04f) / 0.035f))
                return envolvente * (0.4f + 0.6f * Math.pow(lobulo.toDouble(), 0.75).toFloat()) + oreja
            }
            fun y(s: Float) = 0.4f - 0.92f * s
            val pasos = 72
            val hoja = Path().apply {
                moveTo(c.x, c.y + y(0f) * l)
                // Cada lóbulo apunta un poco hacia la punta: cuanto más ancho, más sube.
                for (i in 0..pasos) { val s = i / pasos.toFloat(); val a = ancho(s, 0f); lineTo(c.x - a * l, c.y + (y(s) - 0.3f * a) * l) }
                for (i in pasos downTo 0) { val s = i / pasos.toFloat(); val a = ancho(s, 0.9f); lineTo(c.x + a * l, c.y + (y(s) - 0.3f * a) * l) }
                close()
            }
            drawPath(hoja, color)
            if (l >= 9f) {
                // Media hoja en sombra (la luz viene de la izquierda) y un nervio a cada lóbulo.
                clipRect(c.x, c.y - l, c.x + l, c.y + l) { drawPath(hoja, nervio.copy(alpha = 0.22f)) }
                val fino = max(l * 0.016f, 0.5f)
                for (k in 1..4) {
                    val si = (k + 0.12f) / n
                    val sd = (k + 0.12f - 0.9f / (2 * PI.toFloat())) / n
                    if (si < 0.95f) drawLine(nervio.copy(alpha = 0.5f), Offset(c.x, c.y + y(si - 0.06f) * l), Offset(c.x - ancho(si, 0f) * 0.8f * l, c.y + (y(si) - 0.26f * ancho(si, 0f)) * l), fino, StrokeCap.Round)
                    if (sd < 0.95f) drawLine(nervio.copy(alpha = 0.5f), Offset(c.x, c.y + y(sd - 0.06f) * l), Offset(c.x + ancho(sd, 0.9f) * 0.8f * l, c.y + (y(sd) - 0.26f * ancho(sd, 0.9f)) * l), fino, StrokeCap.Round)
                }
            }
            drawLine(nervio, Offset(c.x, c.y + 0.56f * l), Offset(c.x, c.y - 0.4f * l), max(l * 0.03f, 0.6f), StrokeCap.Round)
        }
    }

    /**
     * El caballo de los estáteros galos, al galope: el cuerpo liso como una almendra, el cuello alto y arqueado con la
     * crin de perlas, la cabeza pequeña con el ojo grande, el hombro y la grupa como dos glóbulos, las patas finas con la
     * rodilla en bolita y el casco redondo, y la cola que se abre en tres hebras con su perla. Es el mismo dibujo del sello
     * (`herramientas/sellos.py`, `caballo`). [c] es el centro de la figura y [largo], su ancho de la nariz a la cola; mira
     * a la izquierda (a la derecha con [mira] = 1). Con [hueco], el ojo, la boca y los surcos del hombro y la grupa se
     * recortan en ese color; sin él, solo la silueta. [recio] engruesa las patas y la cola (para los íconos pequeños).
     */
    fun DrawScope.caballo(c: Offset, largo: Float, color: Color, hueco: Color? = null, mira: Float = -1f, recio: Float = 1f) {
        val u = largo / 121f
        // El centro de la figura queda en (-2.5, -7.5) de las unidades del sello.
        fun q(x: Float, y: Float) = Offset(c.x - mira * (x + 2.5f) * u, c.y + (y + 7.5f) * u)
        fun b(a: Offset, b1: Offset, c1: Offset, d: Offset, n: Int = 10) = bezier(a, b1, c1, d, n).map { q(it.x, it.y) }
        fun o(x: Float, y: Float) = Offset(x, y)
        fun cadena(vararg tramos: List<Offset>): List<Offset> {
            val r = ArrayList<Offset>()
            tramos.forEachIndexed { i, t -> r.addAll(if (i < tramos.size - 1) t.dropLast(1) else t) }
            return r
        }
        // El cuerpo: lomo, grupa, nalga, vientre y pecho.
        drawPath(poligono(cadena(b(o(-30f, -4f), o(-26f, -16f), o(-14f, -15f), o(2f, -12f)), b(o(2f, -12f), o(14f, -10f), o(22f, -17f), o(32f, -13f)),
            b(o(32f, -13f), o(40f, -10f), o(41f, 4f), o(33f, 9f)), b(o(33f, 9f), o(24f, 15f), o(8f, 15f), o(-6f, 14f)),
            b(o(-6f, 14f), o(-18f, 14f), o(-28f, 12f), o(-33f, 4f)), b(o(-33f, 4f), o(-34f, 0f), o(-32f, -2f), o(-30f, -4f)))), color)
        // El cuello, ancho en la cruz, alto y arqueado hacia la cabeza.
        drawPath(poligono(cadena(b(o(-12f, -14f), o(-16f, -30f), o(-26f, -42f), o(-38f, -45f)), b(o(-38f, -45f), o(-43f, -44f), o(-47f, -38f), o(-47f, -30f)),
            b(o(-47f, -30f), o(-44f, -18f), o(-38f, -8f), o(-33f, 3f)))), color)
        // La cabeza: la frente, el hueso de la nariz que baja, el belfo romo y la quijada; y la oreja.
        drawPath(poligono(cadena(b(o(-37f, -46f), o(-45f, -48f), o(-52f, -42f), o(-56f, -34f)), b(o(-56f, -34f), o(-59f, -28f), o(-63f, -24f), o(-61f, -19f)),
            b(o(-61f, -19f), o(-59f, -15f), o(-53f, -16f), o(-50f, -19f)), b(o(-50f, -19f), o(-46f, -22f), o(-44f, -26f), o(-44f, -32f)))), color)
        drawPath(poligono(listOf(q(-41f, -43f), q(-38.5f, -50f), q(-36.5f, -54f), q(-34.5f, -50f), q(-33f, -44f))), color)
        // La crin: perlas a lo largo del cuello, por fuera.
        bezier(o(-14f, -19f), o(-17f, -32f), o(-25f, -42f), o(-34f, -47f), 6).forEachIndexed { i, pt ->
            drawCircle(color, (3.0f - i * 0.2f) * u, q(pt.x - 1.8f, pt.y - 2f))
        }
        // Las patas: las de delante estiradas hacia delante, las de atrás hacia atrás; rodillas en bolita y cascos.
        val patas = listOf(
            listOf(o(-28f, 8f), o(-38f, 13f), o(-46f, 18f)) to listOf(o(-46f, 18f), o(-54f, 16f), o(-60f, 11f), o(-61f, 10f)),
            listOf(o(-20f, 11f), o(-24f, 20f), o(-30f, 26f)) to listOf(o(-30f, 26f), o(-26f, 32f), o(-22f, 37f), o(-21f, 38f)),
            listOf(o(28f, 6f), o(36f, 14f), o(40f, 21f)) to listOf(o(40f, 21f), o(48f, 27f), o(56f, 31f), o(57f, 31f)),
            listOf(o(17f, 12f), o(21f, 21f), o(20f, 27f)) to listOf(o(20f, 27f), o(15f, 32f), o(11f, 38f), o(10f, 39f)),
        )
        for ((arriba, abajo) in patas) {
            fun suave(pts: List<Offset>) = bezier(pts[0], pts[0] + (pts[1] - pts[0]) * 0.66f, pts[2] + (pts[1] - pts[2]) * 0.66f, pts[2], 8).map { q(it.x, it.y) }
            drawPath(cinta(suave(arriba)) { f -> (6.5f - 2.3f * f) * u * recio }, color)
            drawPath(cinta(suave(abajo)) { f -> (3.6f - 0.8f * f) * u * recio }, color)
            drawCircle(color, 3.4f * u * recio, q(arriba.last().x, arriba.last().y))        // la rodilla
            drawCircle(color, 3.0f * u * recio, q(abajo.last().x, abajo.last().y))          // el casco
        }
        // La cola: tres hebras que salen de la grupa, suben y se abren hacia atrás, cada una con su perla.
        listOf(Triple(o(42f, -25f), o(52f, -30f), o(57f, -23f)), Triple(o(46f, -19f), o(55f, -19f), o(58f, -11f)), Triple(o(43f, -11f), o(50f, -5f), o(53f, 1f)))
            .forEachIndexed { k, (c1, c2, fin) ->
                drawPath(cinta(b(o(35f, -11f), c1, c2, fin, 14)) { f -> (3.4f - k * 0.3f - (1.6f - k * 0.3f) * f) * u * recio }, color)
                drawCircle(color, 2.4f * u * recio, q(fin.x, fin.y))
            }
        if (hueco != null) {
            // El ojo grande en anillo, la boca y los surcos en media luna del hombro y de la grupa.
            drawCircle(hueco, 3.8f * u, q(-49f, -35f))
            drawCircle(color, 2.3f * u, q(-49f, -35f))
            drawLine(hueco, q(-62f, -20f), q(-56f, -20f), max(0.9f * u, 0.6f))
            for ((gx, gy, gr) in listOf(Triple(-21f, 1f, 8f), Triple(22f, -1f, 9.6f))) {
                val centro = q(gx, gy); val rr = gr * u
                // En el sello el surco va de -20° a 200° (y de -40° a 160°), medidos como en la imagen; al mirar a la
                // derecha se refleja.
                val (a0, a1) = if (gx < 0) -20f to 200f else -40f to 160f
                val desde = if (mira < 0) a0 else 180f - a1
                drawArc(hueco, desde, a1 - a0, false, Offset(centro.x - rr, centro.y - rr), Size(rr * 2, rr * 2), style = Stroke(max(1.1f * u, 0.6f)))
            }
        }
    }

    /**
     * La rueda de las monedas (lo que quedó del carro del estátero de Filipo que copiaban): la llanta, los [radios] y el
     * cubo. Entre los radios queda lo de atrás; [hueco] es el color del agujero del cubo.
     */
    fun DrawScope.rueda(c: Offset, r: Float, color: Color, hueco: Color, radios: Int = 4, giro: Float = 0.3f) {
        drawCircle(color, r * 0.84f, c, style = Stroke(r * 0.32f))
        for (k in 0 until radios) {
            val a = giro + k * 2 * PI.toFloat() / radios
            drawLine(color, c, Offset(c.x + r * 0.75f * cos(a), c.y + r * 0.75f * sin(a)), r * 0.24f)
        }
        drawCircle(color, r * 0.3f, c)
        if (hueco.alpha > 0f) drawCircle(hueco, r * 0.1f, c)
    }

    /**
     * La «S» tumbada de las monedas galas y del arte de La Tène: un trazo en S que engruesa en el medio, con una perla en
     * cada punta. [largo] es su ancho; [grueso], el del medio; [giro], en grados.
     */
    fun DrawScope.esse(c: Offset, largo: Float, grueso: Float, color: Color, giro: Float = 0f) {
        val u = largo / 32f
        rotate(giro, c) {
            fun o(x: Float, y: Float) = Offset(c.x + x * u, c.y + y * u)
            val pts = bezier(o(-16f, 4f), o(-14f, -12f), o(2f, -8f), o(0f, 0f), 12).dropLast(1) + bezier(o(0f, 0f), o(-2f, 8f), o(14f, 12f), o(16f, -4f), 12)
            drawPath(cinta(pts) { f -> grueso * (0.55f + 0.45f * sin(PI.toFloat() * f)) }, color)
            drawCircle(color, grueso * 0.62f, pts.first()); drawCircle(color, grueso * 0.62f, pts.last())
        }
    }

    /**
     * Una ramita de muérdago: dos tallos que se ahorquillan, cada uno con su par de hojas alargadas y romas, y las bayas
     * blancas en la horquilla. [c] es donde se ahorquilla y [tam] su alto; [giro], en grados (0 = de pie).
     */
    fun DrawScope.muerdago(c: Offset, tam: Float, giro: Float = 0f, hoja: Color = MUERDAGO, baya: Color = BAYA) {
        rotate(giro, c) {
            val tallo = lerp(hoja, Color(0xFF6E7A3A), 0.5f)
            fun o(x: Float, y: Float) = Offset(c.x + x * tam, c.y + y * tam)
            drawLine(tallo, o(0f, 0.45f), c, tam * 0.06f, StrokeCap.Round)
            for (lado in listOf(-1f, 1f)) {
                val punta = o(lado * 0.22f, -0.22f)
                drawLine(tallo, c, punta, tam * 0.05f, StrokeCap.Round)
                for (h in listOf(-1f, 1f)) {
                    val a = Math.toRadians((-90.0 + lado * 25 + h * 38)).toFloat()
                    val fin = Offset(punta.x + cos(a) * tam * 0.36f, punta.y + sin(a) * tam * 0.36f)
                    val mitad = Offset((punta.x + fin.x) / 2, (punta.y + fin.y) / 2)
                    val nx = -(fin.y - punta.y); val ny = fin.x - punta.x
                    val k = 0.3f
                    val hojaP = Path().apply {
                        moveTo(punta.x, punta.y)
                        quadraticTo(mitad.x + nx * k, mitad.y + ny * k, fin.x, fin.y)
                        quadraticTo(mitad.x - nx * k, mitad.y - ny * k, punta.x, punta.y)
                        close()
                    }
                    drawPath(hojaP, if (h * lado > 0) lerp(hoja, Color.Black, 0.18f) else hoja)
                }
            }
            for ((x, y) in listOf(-0.05f to -0.04f, 0.06f to -0.06f, 0.01f to 0.04f)) {
                drawCircle(lerp(baya, Color.Black, 0.25f), tam * 0.075f, o(x + 0.01f, y + 0.012f))
                drawCircle(baya, tam * 0.07f, o(x, y))
                drawCircle(Color.White.copy(alpha = 0.8f), tam * 0.022f, o(x - 0.02f, y - 0.02f))
            }
        }
    }

    /**
     * El carnyx: la trompeta de guerra de bronce, alta sobre la gente, con la boca en cabeza de jabalí (como los de
     * Tintignac y de Deskford): la boquilla abajo, doblada; el tubo largo de pie con sus anillos; y arriba, el cuello que
     * se dobla hacia delante y la cabeza en cuña, sin frente marcada, que baja recta hasta el hocico largo y truncado con
     * el disco de la nariz; las fauces abiertas (por ahí sale el bramido) con la lengua suelta dentro, los colmillos que
     * se curvan hacia arriba, el ojo chico, las orejas cortas y paradas y la cresta de cerdas por la nuca. [pie] es el
     * extremo de abajo; [alto], su alto; mira a la derecha (a la izquierda con [mira] = -1). Con [hueco], el ojo y la boca
     * se recortan en ese color. [brillo] y [sombra] son la luz y la sombra del metal (si no, salen de [color]). Con un
     * [color] translúcido se dibuja solo la silueta, de una pieza (para su sombra).
     */
    fun DrawScope.carnyx(pie: Offset, alto: Float, color: Color = BRONCE, hueco: Color? = null, mira: Float = 1f, brillo: Color? = null, sombra: Color? = null) {
        val u = alto / 100f
        fun o(x: Float, y: Float) = Offset(pie.x + mira * x * u, pie.y + y * u)
        fun Path.m(x: Float, y: Float) { val q = o(x, y); moveTo(q.x, q.y) }
        fun Path.l(x: Float, y: Float) { val q = o(x, y); lineTo(q.x, q.y) }
        fun Path.q(x1: Float, y1: Float, x: Float, y: Float) { val a = o(x1, y1); val b = o(x, y); quadraticTo(a.x, a.y, b.x, b.y) }
        fun Path.c(x1: Float, y1: Float, x2: Float, y2: Float, x: Float, y: Float) { val a = o(x1, y1); val b = o(x2, y2); val d = o(x, y); cubicTo(a.x, a.y, b.x, b.y, d.x, d.y) }
        val claro = brillo ?: lerp(color, Color.White, 0.45f)
        val osc = sombra ?: lerp(color, Color.Black, 0.42f)
        // Las piezas de la silueta.
        val boquilla = cinta(bezier(o(-9f, 0f), o(-8f, -6f), o(-1f, -6f), o(0f, -14f), 8)) { f -> (3.2f + 0.6f * f) * u }
        val copa = Path().apply { m(-11.4f, 1.2f); l(-6.6f, 1.2f); l(-7.6f, -1f); l(-10.4f, -1f); close() }
        val tubo = cinta(bezier(o(0f, -14f), o(0f, -40f), o(0f, -58f), o(2f, -70f), 16)) { f -> (3.8f + 2.2f * f) * u }
        val cuello = cinta(bezier(o(2f, -68f), o(2.4f, -77f), o(4.6f, -82f), o(11f, -85f), 10)) { f -> (6f + 8f * f) * u }
        // La cabeza en cuña: la nuca, la línea de arriba que baja recta al hocico, el disco, la quijada de arriba hasta la
        // comisura y la de abajo, abierta, con la papada.
        val cabeza = Path().apply {
            m(4.6f, -79f); q(2.6f, -90f, 8.4f, -95.6f); l(13.5f, -98.6f)
            c(22f, -97.4f, 32f, -93.6f, 40.6f, -91.2f)
            l(42.4f, -90.8f); q(43.8f, -88.2f, 42.6f, -85.6f)
            l(40.2f, -85.2f); c(34f, -85.6f, 28.5f, -85.2f, 24.4f, -83.8f)
            c(29f, -82.4f, 34f, -80.6f, 37.6f, -79f)
            q(39f, -78.2f, 37.8f, -76.9f)
            c(31f, -75.4f, 22f, -73.8f, 14f, -74.8f); q(8f, -75.6f, 4.6f, -79f); close()
        }
        val boca = Path().apply { m(24.2f, -83.8f); c(28.5f, -85.2f, 34f, -85.6f, 40.4f, -85.2f); l(38.2f, -78.8f); c(34f, -80.6f, 29f, -82.4f, 24.2f, -83.8f); close() }
        val oreja = Path().apply { m(14.4f, -96.8f); q(12.6f, -100.6f, 12.8f, -103.4f); q(18.4f, -101.8f, 20.6f, -95.8f); close() }
        val orejaLejos = Path().apply { m(10.8f, -97.6f); q(8.8f, -101f, 8.8f, -103.2f); q(13.8f, -101.8f, 15.6f, -97.2f); close() }
        // La cresta de cerdas: púas que salen hacia atrás por la nuca y el lomo del cuello.
        val cresta = Path().apply {
            val base = bezier(Offset(17f, -98.2f), Offset(9f, -98.6f), Offset(2.4f, -93f), Offset(-0.6f, -81f), 14)
            val dentro = bezier(Offset(17f, -95f), Offset(10f, -95f), Offset(5.4f, -91f), Offset(3f, -81f), 6)
            m(base[0].x, base[0].y)
            for (k in 0 until base.size - 1) {
                val a = base[k]; val b = base[k + 1]
                val dx = b.x - a.x; val dy = b.y - a.y; val n = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
                val largo = 3.2f + 2.6f * sin(PI.toFloat() * (k + 0.5f) / (base.size - 1))
                val mitad = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
                l(mitad.x - dy / n * largo + dx / n * 1.6f, mitad.y + dx / n * largo + dy / n * 1.6f)
                l(b.x, b.y)
            }
            for (k in dentro.size - 1 downTo 0) l(dentro[k].x, dentro[k].y)
            close()
        }
        val colmillo = Path().apply { m(35.6f, -79.6f); c(38.8f, -83.6f, 38f, -89.4f, 33.4f, -93f); c(35.4f, -88.8f, 35.6f, -84.6f, 33.2f, -80.6f); close() }
        val colmilloArriba = Path().apply { m(32.8f, -85.4f); q(36.2f, -87.2f, 35.6f, -90.2f); q(34.4f, -87.8f, 31.2f, -85.6f); close() }
        if (color.alpha < 0.99f) {
            // La silueta sola, de una pieza, para que no se oscurezca donde las piezas se tocan.
            var todo = Path.combine(PathOperation.Union, boquilla, tubo)
            for (pieza in listOf(copa, cuello, cabeza, boca, oreja, orejaLejos, cresta, colmillo)) todo = Path.combine(PathOperation.Union, todo, pieza)
            drawPath(todo, color)
            return
        }
        val grande = alto > 70f * density
        // El tubo, con la luz a la izquierda.
        val luzTubo = Brush.horizontalGradient(listOf(lerp(color, claro, 0.6f), color, color, lerp(color, osc, 0.8f)), pie.x - 4.2f * u, pie.x + 4.2f * u)
        drawPath(orejaLejos, lerp(color, osc, 0.55f))
        drawPath(copa, luzTubo)
        drawPath(boquilla, luzTubo)
        drawPath(tubo, luzTubo)
        drawPath(cuello, Brush.linearGradient(listOf(lerp(color, claro, 0.45f), color, lerp(color, osc, 0.7f)), o(3f, -86f), o(9f, -74f)))
        drawPath(cresta, Brush.linearGradient(listOf(lerp(color, claro, 0.3f), lerp(color, osc, 0.5f)), o(6f, -100f), o(8f, -84f)))
        // La cabeza: la luz arriba a la izquierda y la sombra en la papada.
        val cc = o(22f, -88f)
        drawPath(cabeza, Brush.radialGradient(listOf(lerp(color, claro, 0.7f), color, lerp(color, osc, 0.85f)), Offset(cc.x - 7f * u, cc.y - 8f * u), 30f * u))
        // Las fauces abiertas y la lengua suelta, como la de Deskford.
        val dentroBoca = hueco ?: lerp(osc, Color.Black, 0.45f)
        drawPath(boca, dentroBoca)
        drawPath(Path().apply { m(26f, -83.2f); q(32f, -82.6f, 36.6f, -80.2f); q(37.4f, -78.8f, 35.6f, -79.2f); q(31f, -80.4f, 26f, -82.4f); close() }, lerp(color, osc, 0.35f))
        drawPath(Path().apply { m(27f, -83.3f); q(32f, -82.8f, 36f, -80.6f) }, claro.copy(alpha = 0.45f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
        // El metal martillado: las abolladuras chicas del repujado, solo cuando se ve grande.
        if (grande) {
            val az = java.util.Random(31)
            clipPath(cabeza) {
                repeat(70) {
                    val q = o(6f + az.nextFloat() * 36f, -98f + az.nextFloat() * 23f); val r = (0.5f + az.nextFloat() * 0.6f) * u
                    drawCircle(osc.copy(alpha = 0.12f), r, q + Offset(0.3f * r, 0.3f * r))
                    drawCircle(claro.copy(alpha = 0.14f), r * 0.5f, q - Offset(0.3f * r, 0.3f * r))
                }
            }
            clipPath(tubo) {
                repeat(28) {
                    val q = o(-3f + az.nextFloat() * 7f, -16f - az.nextFloat() * 52f); val r = (0.4f + az.nextFloat() * 0.5f) * u
                    drawCircle(osc.copy(alpha = 0.1f), r, q + Offset(0.3f * r, 0.3f * r))
                }
            }
        }
        // La sombra bajo la quijada de arriba y la luz que rebota en la papada.
        drawPath(Path().apply { m(24.6f, -84.2f); c(29f, -85.6f, 34f, -86.1f, 40.4f, -85.7f) }, osc.copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { m(14f, -75.6f); c(22f, -74.7f, 30f, -76.1f, 36.6f, -77.6f) }, claro.copy(alpha = 0.35f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // El hocico: los pliegues de la jeta y el disco de la nariz, de frente, con sus dos ventanas.
        for (x in listOf(32.5f, 35.2f, 37.8f)) drawPath(Path().apply { m(x - 0.6f, -93.4f + (x - 32f) * 0.28f); q(x + 0.4f, -90.6f + (x - 32f) * 0.2f, x - 0.2f, -88f) }, osc.copy(alpha = 0.55f), style = Stroke(0.55f * u, cap = StrokeCap.Round))
        val disco = o(42.5f, -88.2f)
        drawOval(lerp(color, osc, 0.3f), Offset(disco.x - 1.5f * u, disco.y - 2.7f * u), Size(3f * u, 5.4f * u))
        drawOval(claro.copy(alpha = 0.7f), Offset(disco.x - 1.5f * u, disco.y - 2.7f * u), Size(3f * u, 5.4f * u), style = Stroke(0.45f * u))
        for (dy in listOf(-1.2f, 1.1f)) drawCircle(hueco ?: osc, 0.55f * u, Offset(disco.x + mira * 0.2f * u, disco.y + dy * u))
        // El ojo chico, hundido bajo la ceja.
        drawPath(Path().apply { m(22.2f, -93.6f); q(25.6f, -94.9f, 28.6f, -93f) }, osc.copy(alpha = 0.75f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawCircle(hueco ?: osc, 1.3f * u, o(25.6f, -91.6f))
        drawCircle(claro, 0.42f * u, o(25.2f, -92f))
        // Las orejas cortas y paradas, con su hueco.
        drawPath(oreja, Brush.linearGradient(listOf(lerp(color, claro, 0.6f), lerp(color, osc, 0.4f)), o(13f, -103f), o(20f, -96f)))
        drawPath(Path().apply { m(15.6f, -97.2f); q(14.6f, -100f, 14.6f, -101.6f); q(17.8f, -100.4f, 19.2f, -96.8f); close() }, lerp(color, osc, 0.75f))
        // Los colmillos, curvos hacia arriba, de un tono más claro.
        val marfil = lerp(color, claro, 0.75f)
        drawPath(colmilloArriba, lerp(marfil, osc, 0.15f))
        drawPath(colmillo, Brush.linearGradient(listOf(lerp(marfil, Color.White, 0.2f), marfil, lerp(marfil, osc, 0.45f)), o(33f, -92f), o(38f, -80f)))
        drawPath(colmillo, osc.copy(alpha = 0.55f), style = Stroke(0.35f * u))
        // Los anillos del tubo y el collar donde nace el cuello, en relieve.
        for ((y, ancho) in listOf(-14f to 4.6f, -28f to 5f, -50f to 5.6f, -66f to 7f)) {
            val x = if (y < -60f) 1.4f else 0f
            drawRoundRect(lerp(color, osc, 0.5f), o(x - ancho / 2, y - 0.8f) + Offset(if (mira < 0) -ancho * u else 0f, 0f), Size(ancho * u, 2.2f * u), androidx.compose.ui.geometry.CornerRadius(0.8f * u))
            drawLine(lerp(color, claro, 0.75f), o(x - ancho / 2 + 0.4f, y - 0.4f), o(x + ancho / 2 - 0.4f, y - 0.4f), 0.6f * u, StrokeCap.Round)
        }
        // El brillo del bronce por el lado de la luz: el tubo, el lomo de la cabeza y la oreja.
        drawPath(cinta(bezier(o(-2f, -16f), o(-2f, -40f), o(-1.6f, -56f), o(-0.4f, -66f), 10)) { 0.9f * u }, claro.copy(alpha = 0.7f))
        drawPath(cinta(bezier(o(10f, -95.6f), o(18f, -97.4f), o(28f, -94.4f), o(39.6f, -91.4f), 10)) { f -> 1.6f * u * sin(PI.toFloat() * f) }, claro.copy(alpha = 0.75f))
        drawPath(cinta(bezier(o(14.6f, -97f), o(13.6f, -99f), o(13.2f, -101f), o(13.2f, -103f), 6)) { f -> 0.7f * u * sin(PI.toFloat() * f) }, claro.copy(alpha = 0.6f))
    }

    /**
     * El emblema del tema: el caballo de los estáteros arvernos, en oro, con la perla sobre el lomo y la rueda debajo,
     * dentro del torques. Pequeño (menos de 48 dp de lado), queda el torques solo, como en el ícono, con una perla en el
     * centro. [t] (0 a 1) pasa un brillo por el aro y hace que el caballo dé un paso de galope (sube y baja apenas).
     * [oscuro] baja el oro de los contornos en el modo claro para que se lea sobre el barro.
     */
    fun DrawScope.emblema(c: Offset, r: Float, t: Float, oscuro: Boolean) {
        val pequeno = r * 2 < 48f * density
        val oro = if (oscuro) ORO else lerp(ORO, Color(0xFF7E590C), 0.25f)
        val contorno = lerp(ORO, Color.Black, if (oscuro) 0.55f else 0.5f)
        val brillo = ORO_BRILLO
        val g = r * (if (pequeno) 0.17f else 0.11f)
        val ra = r - g * 1.25f
        if (!pequeno) {
            // El fondo: una rodaja de roble oscuro dentro del aro, con su veta y la sombra del aro por dentro.
            val cf = Offset(c.x, c.y - g * 0.3f); val rf = ra - g * 0.3f
            drawCircle(Brush.radialGradient(listOf(Color(0xFF4A3420), Color(0xFF2A1B0E)), cf + Offset(-rf * 0.3f, -rf * 0.35f), rf * 1.5f), rf, cf)
            for (k in 1..5) drawCircle(Color(0xFF1E130A).copy(alpha = 0.35f), rf * (0.18f * k + (k % 2) * 0.03f), cf + Offset(rf * 0.12f, rf * 0.08f), style = Stroke(max(rf * 0.008f, 0.5f)))
            drawCircle(Color.Black.copy(alpha = 0.35f), rf, cf, style = Stroke(rf * 0.05f))
        }
        torques(Offset(c.x, c.y - g * 0.3f), ra, g, oro, brillo)
        // El brillo que corre por el aro: una mancha clara que da la vuelta una vez por ciclo.
        val ang = Math.toRadians((112.0 + t * 316.0)).toFloat()
        val pb = Offset(c.x + ra * cos(ang), c.y - g * 0.3f + ra * sin(ang))
        val luz = sin(PI.toFloat() * t).coerceAtLeast(0f)
        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.55f * luz), Color.White.copy(alpha = 0f)), pb, g * 1.4f), g * 1.4f, pb)
        if (pequeno) {
            drawCircle(contorno, g * 0.62f, Offset(c.x, c.y - g * 0.3f))
            drawCircle(oro, g * 0.5f, Offset(c.x, c.y - g * 0.3f))
            drawCircle(brillo.copy(alpha = 0.8f), g * 0.18f, Offset(c.x - g * 0.15f, c.y - g * 0.45f))
            return
        }
        // El caballo, al galope: sube y baja dos veces por ciclo.
        val salto = -abs(sin(2 * PI.toFloat() * t)) * r * 0.025f
        val largo = ra * 1.45f
        val cc = Offset(c.x - ra * 0.04f, c.y - ra * 0.04f + salto)
        caballo(cc + Offset(r * 0.018f, r * 0.026f), largo, Color.Black.copy(alpha = 0.22f))       // la sombra corta
        caballo(cc, largo, contorno)
        caballo(cc + Offset(-r * 0.008f, -r * 0.01f), largo * 0.985f, oro, lerp(contorno, oro, 0.25f))
        // El brillo del relieve: el lomo y el cuello, del lado de la luz.
        val u = largo / 121f
        fun q(x: Float, y: Float) = Offset(cc.x + (x + 2.5f) * u, cc.y + (y + 7.5f) * u)
        drawPath(cinta(bezier(q(-28f, -6f), q(-22f, -14f), q(-8f, -13f), q(6f, -10f), 10)) { f -> 2.4f * u * sin(PI.toFloat() * f) }, brillo.copy(alpha = 0.7f))
        drawPath(cinta(bezier(q(-16f, -22f), q(-20f, -32f), q(-27f, -40f), q(-36f, -43f), 10)) { f -> 2f * u * sin(PI.toFloat() * f) }, brillo.copy(alpha = 0.6f))
        // La perla sobre el lomo y la rueda debajo, en relieve.
        val perla = Offset(cc.x - ra * 0.02f, cc.y - ra * 0.5f)
        drawCircle(contorno, ra * 0.075f, perla)
        drawCircle(oro, ra * 0.062f, perla + Offset(-ra * 0.006f, -ra * 0.008f))
        drawCircle(brillo, ra * 0.022f, perla + Offset(-ra * 0.022f, -ra * 0.025f))
        val pr = Offset(cc.x + ra * 0.02f, cc.y + ra * 0.6f)
        rueda(pr + Offset(r * 0.012f, r * 0.016f), ra * 0.15f, Color.Black.copy(alpha = 0.2f), Color.Transparent)
        rueda(pr, ra * 0.15f, contorno, Color.Transparent)
        rueda(pr + Offset(-r * 0.006f, -r * 0.008f), ra * 0.14f, oro, Color.Transparent)
        drawCircle(brillo.copy(alpha = 0.6f), ra * 0.03f, pr + Offset(-ra * 0.05f, -ra * 0.06f))
    }
}
