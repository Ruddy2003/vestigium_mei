package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * Dónde va cada cosa del rincón de la libreta de Templario, en píxeles, para un lienzo de [w] por [h] y una regla de [hojas]
 * artículos. Todo se mide en [U] (un dp del alto de diseño), así el rincón no se deforma si cambia el ancho: el púlpito y la
 * regla van al centro y lo de los lados se corta por el borde.
 */
internal class SitioLibretaTemplario(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaTemplario.ALTO.value * u)).coerceIn(0.5f, 2f)
    val cx = w * 0.5f
    /** El ancho de la regla y su grueso (engorda con los artículos: de 12 a 32 dp). */
    val bw = minOf(232f * U, w * 0.62f)
    val g = (12f + 20f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    /** El grueso en escorzo: hacia dónde se ve el bloque de las hojas (abajo y a la derecha de la tapa). */
    val v = Offset(0.45f * g, 0.6f * g)
    /** El ancho que se ve del lomo redondo, a la izquierda de la tapa. */
    val lomo = 4f * U + 0.3f * g
    /** El canto del atril (donde apoya la regla) y el pretil del púlpito. */
    val listonArriba = 198f * U
    val listonAbajo = 208f * U
    val pretilArriba = 208f * U
    val pretilFrente = 216f * U
    val pretilAbajo = 250f * U
    val repisaAbajo = 257f * U
    /** La tapa de la regla, tendida en el atril: su canto de abajo (la testa) se ve debajo, con el grueso de las hojas. */
    val libro = Rect(cx - (bw + v.x - lomo) / 2, listonArriba - v.y - 0.5f * U - 158f * U, cx + (bw - v.x + lomo) / 2, listonArriba - v.y - 0.5f * U)
    /** El atril: el tablero inclinado (de su canto de arriba al listón) y sus dos costados de cedro. */
    val atrilIzq = cx - bw / 2 - 28f * U
    val atrilDer = cx + bw / 2 + 28f * U
    val atrilArriba = 44f * U
    /** El arco del púlpito: el hueco por dentro, su arranque y el grueso de las dovelas. */
    val huecoIzq = cx - bw / 2 - 36f * U
    val huecoDer = cx + bw / 2 + 36f * U
    val arranque = 112f * U
    val dovela = 20f * U
    /** El pretil, algo más ancho que el arco. */
    val pretilIzq = huecoIzq - dovela - 14f * U
    val pretilDer = huecoDer + dovela + 14f * U
    /** La lámpara de bronce de dos mecheros, en el pretil a la derecha, y sus dos llamas. */
    val lampara = Offset(huecoDer - 20f * U, pretilArriba)
    /** La lámpara se dibuja a su tamaño de diseño y se agranda [ESCALA] veces desde su pie: [llamas] van en ese dibujo. */
    val llamas = listOf(Offset(lampara.x - 17f * U, 121f * U), Offset(lampara.x + 17f * U, 121f * U))
    /** Dónde arden, ya agrandadas: el centro de las dos llamas (de aquí sale la luz de la noche). */
    val llama = Offset(lampara.x, lampara.y - (lampara.y - 116f * U) * ESCALA)

    companion object {
        const val ESCALA = 1.3f
    }
    /** El pan y la escudilla, delante a la izquierda. */
    val pan = Offset(huecoIzq + 22f * U, pretilArriba + 1f * U)
    /** Las tablillas de cera, colgadas fuera del arco a la derecha (si caben). */
    val tablillas = Offset(pretilDer + 8f * U, 52f * U)
    val conTablillas = tablillas.x + 30f * U < w + 10f * U

    /** El radio de los dos arcos del arco apuntado (más corto que la luz: un apuntado algo rebajado). */
    val radio = (huecoDer - huecoIzq) * 0.8f
    /** Los centros de los dos arcos: el de la mitad izquierda y el de la derecha. */
    val centroIzq = Offset(huecoIzq + radio, arranque)
    val centroDer = Offset(huecoDer - radio, arranque)

    /** El arco apuntado con los dos centros de siempre y radio [r] (el de dentro o el de fuera), con sus jambas hasta el pretil. */
    private fun apuntado(r: Float): Path = Path().apply {
        val v1 = ((cx - centroIzq.x) / r).coerceIn(-1f, 1f)
        val f1 = 360f - Math.toDegrees(kotlin.math.acos(v1.toDouble())).toFloat()
        val v2 = ((cx - centroDer.x) / r).coerceIn(-1f, 1f)
        val f2 = 360f - Math.toDegrees(kotlin.math.acos(v2.toDouble())).toFloat()
        moveTo(centroIzq.x - r, pretilArriba + 1f); lineTo(centroIzq.x - r, arranque)
        arcTo(Rect(centroIzq.x - r, centroIzq.y - r, centroIzq.x + r, centroIzq.y + r), 180f, f1 - 180f, false)
        arcTo(Rect(centroDer.x - r, centroDer.y - r, centroDer.x + r, centroDer.y + r), f2, 360f - f2, false)
        lineTo(centroDer.x + r, pretilArriba + 1f); close()
    }

    /** El arco por dentro (el hueco del púlpito), recortado arriba por el lienzo. */
    fun hueco(): Path = apuntado(radio)
    /** El arco por fuera (las dovelas y las jambas). */
    fun fuera(): Path = apuntado(radio + dovela)

    /**
     * La luz de la ventana que queda fuera, a la izquierda: una lanceta proyectada, tendida y alargada hacia abajo a la derecha,
     * con su cabeza de arco arriba. Cae en el fondo del hueco, en el arco, en el atril y en la regla: cada plano que está más
     * adelante la recibe corrida hacia la ventana ([corrido] a lo ancho, [alzado] a lo alto), así se quiebra al pasar de uno a otro.
     */
    fun lanceta(oscuro: Boolean, corrido: Float = 0f, alzado: Float = 0f): Path {
        val p = Path()
        // Cae corta: de la cabeza de arco, arriba a la izquierda, baja hasta media tapa (de noche, la luna, algo más alta).
        val (x0, y0, x1, y1, an) = if (!oscuro) floatArrayOf(huecoIzq - 4f * U, 22f * U, huecoIzq + 50f * U, 124f * U, 46f * U).toList()
            else floatArrayOf(huecoIzq + 4f * U, 12f * U, huecoIzq + 46f * U, 104f * U, 40f * U).toList()
        val n = 8
        for (j in 0..n) {
            val t = j / n.toFloat()
            val a = Math.PI * (1.0 - t)
            val px = x0 + an / 2 + kotlin.math.cos(a).toFloat() * an / 2 + corrido
            val py = y0 + an * 0.55f - kotlin.math.sin(a).toFloat() * an * 0.55f + alzado
            if (j == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        p.lineTo(x1 + an + corrido, y1 + alzado); p.lineTo(x1 + corrido, y1 + alzado); p.close()
        return p
    }
}

/**
 * El color de una superficie dentro de la lanceta, a partir de [base] (su color sin esa luz): de día, el sol, más claro, más
 * cálido y más saturado (la badana pasa a bermellón, el latón a oro); de noche, la luna, fría, que se suma a lo que hay. Es el
 * color de la superficie, opaco: no un velo encima. [f], cuánta le llega (de 0 a 1).
 */
internal fun AireTemplario.enLanceta(base: Color, f: Float = 1f): Color =
    if (!oscuro) mezcla(base, Color((base.red * 1.5f + 0.07f).coerceAtMost(1f), (base.green * 1.22f + 0.03f).coerceAtMost(1f), (base.blue * 0.95f).coerceAtMost(1f), base.alpha), f)
    else mezcla(base, Color(0xFF7C90CC), 0.44f * f)


/**
 * La libreta de momentos de Templario: la regla de la casa en el púlpito del lector del refectorio, en el mismo castillo que
 * el Ideario y la biblioteca. El púlpito está metido en el muro bajo su arco apuntado de dovelas de caliza, con su pretil
 * sobre una ménsula; encima del pretil, el atril de cedro con su canto, y en él la regla tendida: tablas forradas de badana
 * teñida de rubia, con su gofrado en seco, cinco bullones y cantoneras de latón, dos cierres de correa y, arriba, el tejuelo de
 * pergamino bajo su lámina de cuerno con el nombre y la cuenta. Engorda con los artículos (más canto, más cédulas, más
 * registros). Del canto cuelga el paño verde del atril; a la derecha, la lámpara de bronce de dos mecheros; delante a la
 * izquierda, el pan y la escudilla del lector, que come después de leer; fuera del arco, las tablillas de cera colgadas.
 *
 * La luz: de día entra por una ventana que queda a la izquierda y deja su lanceta de sol en el fondo del hueco, el arco, el
 * atril, el lomo y la tapa (quebrada en cada plano; enciende el latón que toca);
 * de noche arden los dos mecheros (su charco enciende el fondo, el canto derecho de la regla, el tejuelo y el pan) y la
 * lanceta es de luna, fría, y cae más alta; lo demás se hunde en el añil de la noche. Lo fijo (el muro, el arco, el pretil, el
 * atril y el paño) se pinta una vez por tamaño y modo; la regla, la lámpara y el pan, en cada dibujo.
 */
internal object MuebleLibretaTemplario {
    private val P = PaletaTemplario

    val ALTO: Dp = 262.dp

    /** Cuánta luz de los mecheros le llega a [p] (1 junto a la llama, poca lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaTemplario, a: AireTemplario): Float {
        if (!a.oscuro) return 1f
        val dx = (p.x - s.llama.x) / s.U; val dy = (p.y - s.llama.y) / s.U
        return (1.55f * exp(-0.0135f * sqrt(dx * dx * 0.9f + dy * dy))).coerceIn(0.03f, 1f)
    }

    // ---------------------------------------------------------------- lo fijo, pintado una vez

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaTemplario(w, h, density, e.hojas)
        val a = AireTemplario(oscuro, false)
        val ac = AireTemplario(oscuro, false, cuerpo = true)
        clipRect(0f, 0f, w, h) {
            // El muro, el arco, el pretil, el atril y el paño: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(SalaLibretaTemplario) { sala(s, a, ac) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // La regla, la lámpara y el pan, en vivo.
            with(ReglaLibretaTemplario) { regla(e, s, ac, medidor) }
            with(PiezasLibretaTemplario) {
                lampara(s, ac)
                pan(s, ac)
            }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        }
    }
}

/**
 * La sala de la libreta de Templario ([MuebleLibretaTemplario]): el muro de cal con su despiece fingido a la almagra, el arco
 * del púlpito con sus dovelas y sus impostas, el fondo del hueco en sombra, la lanceta de sol (o de luna), el pretil de
 * caliza sobre su ménsula, el atril de cedro con su canto y el paño verde que cuelga, y las tablillas de cera.
 */
internal object SalaLibretaTemplario {
    private val P = PaletaTemplario
    private val PANO = Color(0xFF3E5A3A)
    private val PANO_ORLA = Color(0xFF8E2A1E)

    fun DrawScope.sala(s: SitioLibretaTemplario, a: AireTemplario, ac: AireTemplario) {
        val w = s.w; val h = s.h
        val fuera = s.fuera(); val hueco = s.hueco()
        muro(s, a)
        // La sombra del arco y del pretil en el muro (a la derecha de día; de noche, poca).
        if (!a.oscuro) { val sp = Path().apply { addPath(fuera, Offset(9f * s.U, 6f * s.U)) }; clipRect(0f, 0f, w, s.pretilArriba) { drawPath(sp, a.sombra(0.22f)) } }
        arco(s, a, fuera, hueco)
        fondo(s, a, hueco)
        pretil(s, a)
        atril(s, ac)
        pano(s, ac)
        if (s.conTablillas) with(PiezasLibretaTemplario) { tablillas(s, a) }
        // El charco de los mecheros en el muro de fuera (en el hueco lo pinta [fondo]).
        if (a.oscuro) {
            val r = 150f * s.U
            clipRect(0f, 0f, w, s.pretilArriba) {
                drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.18f), P.LLAMA.copy(alpha = 0f)), s.llama, r), r, s.llama)
            }
        }
    }

    /** El color de algo del muro según la luz que le llega en [p]. */
    private fun SitioLibretaTemplario.c(a: AireTemplario, col: Color, f: Float, p: Offset) = a.dia(col, f, MuebleLibretaTemplario.cerca(p, this, a))

    /** El muro de fuera: la cal, su despiece fingido a la almagra en doble línea, dos desconchones y la humedad de abajo. */
    private fun DrawScope.muro(s: SitioLibretaTemplario, a: AireTemplario) {
        val w = s.w; val h = s.h; val U = s.U
        if (a.oscuro) {
            val radios = floatArrayOf(0f, 40f, 90f, 150f, 230f, 330f, 460f)
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 460f to a.luz(mezcla(P.CAL, P.CAL_SOMBRA, 0.25f), MuebleLibretaTemplario.cerca(Offset(s.llama.x + radios[i] * U, s.llama.y), s, a)) },
                center = s.llama, radius = 460f * U), Offset.Zero, Size(w, h))
        } else {
            drawRect(Brush.horizontalGradient(listOf(a.dia(mezcla(P.CAL, P.CAL_SOMBRA, 0.3f), 0f), a.dia(mezcla(P.CAL, P.CAL_SOMBRA, 0.62f), 0f)), 0f, w), Offset.Zero, Size(w, h))
        }
        // El moteado de las manos de cal.
        val motas = Path()
        for (j in 0 until 16) {
            val mx = w * azar(8100, j); val my = h * azar(8101, j)
            val mw = (40f + 60f * azar(8102, j)) * U; val mh = mw * 0.35f
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFEDE2CE).copy(alpha = 0.4f), blendMode = BlendMode.Multiply)
        despiece(Rect(0f, 0f, w, h), 0.75f, U)
        // La humedad de abajo, que sube desde la ménsula.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFFD0BC98).copy(alpha = 0.6f)), h * 0.78f, h), Offset(0f, h * 0.78f), Size(w, h * 0.22f), blendMode = BlendMode.Multiply)
        // Dos desconchones donde asoma la caliza.
        listOf(Triple(s.pretilIzq - 40f * U, 64f * U, 8110), Triple(s.pretilDer + 38f * U, 168f * U, 8120)).forEach { (x, y, sem) ->
            val r = Rect(x - 16f * U, y - 9f * U, x + 16f * U, y + 9f * U)
            val p = Path()
            for (j in 0..10) {
                val t = j / 10f * 6.2832f
                val rr = 0.75f + 0.25f * azar(sem, j % 10)
                val q = Offset(r.center.x + kotlin.math.cos(t) * r.width / 2 * rr, r.center.y + kotlin.math.sin(t) * r.height / 2 * rr)
                if (j == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
            }
            p.close()
            drawPath(p, s.c(a, mezcla(P.CALIZA, P.CALIZA_OSC, 0.5f), 0f, r.center))
            clipPath(p) { drawPath(Path().apply { addPath(p, Offset(-1.8f * U, -2f * U)) }, a.sombra(0.5f), style = Stroke(4f * U)) }
            clipRect(r.center.x, r.center.y, r.right + 2f, r.bottom + 2f) { drawPath(p, s.c(a, P.CAL_LUZ, 0f, r.center).copy(alpha = 0.8f), style = Stroke(1.2f * U)) }
        }
    }

    /** El despiece fingido de sillares en [r]: hiladas de 30 dp, juntas al tresbolillo, a la almagra en doble línea. */
    private fun DrawScope.despiece(r: Rect, alfa: Float, U: Float) {
        val lineas = Path(); val pardos = Path()
        var y = r.top + 8f * U; var fila = 0
        while (y < r.bottom) {
            lineas.moveTo(r.left, y); lineas.lineTo(r.right, y + 0.6f * U)
            lineas.moveTo(r.left, y + 2.4f * U); lineas.lineTo(r.right, y + 3f * U)
            var x = r.left + (if (fila % 2 == 0) 22f else 64f) * U
            var xa = r.left; var q = 0
            while (x < r.right) {
                lineas.moveTo(x, y + 2.4f * U); lineas.lineTo(x + 0.4f * U, y + 30f * U)
                lineas.moveTo(x + 2.4f * U, y + 2.4f * U); lineas.lineTo(x + 2.8f * U, y + 30f * U)
                if (azar(8200 + fila, q) < 0.3f) pardos.addRect(Rect(xa + 2.6f * U, y + 3.6f * U, x - 1f * U, y + 29f * U))
                xa = x; x += (80f + 12f * azar(8210 + fila, q)) * U; q++
            }
            y += 30f * U; fila++
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(pardos, Color(0xFFE6D8C4).copy(alpha = 0.6f), blendMode = BlendMode.Multiply)
            drawPath(lineas, Color(0xFFB4533B).copy(alpha = alfa), style = Stroke(1f * U), blendMode = BlendMode.Multiply)
        }
    }

    /** El arco del púlpito: las dovelas de caliza dorada con sus juntas, el intradós y las impostas del arranque. */
    private fun DrawScope.arco(s: SitioLibretaTemplario, a: AireTemplario, fuera: Path, hueco: Path) {
        val U = s.U
        val anillo = Path().apply { op(fuera, hueco, androidx.compose.ui.graphics.PathOperation.Difference) }
        drawPath(anillo, s.c(a, mezcla(P.CALIZA, P.CAL, 0.15f), 0f, Offset(s.cx, 60f * U)))
        clipPath(anillo) {
            // La luz de la izquierda en las dovelas de ese lado; la sombra a la derecha.
            if (!a.oscuro) {
                drawRect(a.claro(0.25f), Offset(s.huecoIzq - s.dovela, 0f), Size(s.dovela, s.pretilArriba))
                drawRect(a.sombra(0.18f), Offset(s.huecoDer, 0f), Size(s.dovela + 2f, s.pretilArriba))
            } else {
                drawRect(a.dia(P.CALIZA, 0f, MuebleLibretaTemplario.cerca(Offset(s.huecoDer, s.llama.y), s, a)).copy(alpha = 0.55f), Offset(s.huecoDer, 0f), Size(s.dovela + 2f, s.pretilArriba))
            }
            // La lanceta en las dovelas de la izquierda: el arco está más adelante que el fondo, así que la recibe corrida
            // hacia la ventana y se quiebra en la arista del intradós.
            val l = s.lanceta(a.oscuro, -15f * U, -4f * U)
            clipPath(l) {
                val base = s.c(a, mezcla(P.CALIZA, P.CAL, 0.15f), if (a.oscuro) 0f else 0.12f, Offset(s.huecoIzq, 60f * U))
                drawRect(if (a.oscuro) a.enLanceta(base, 0.9f) else a.sol(P.CALIZA, base, 1f), Offset(s.huecoIzq - s.dovela - 30f * U, 0f), Size(s.dovela + 90f * U, s.pretilArriba))
            }
            if (!a.oscuro) drawPath(l, Color(0xFFF6DDA8).copy(alpha = 0.5f), style = Stroke(1f * U))
            // Las juntas: en las jambas, horizontales; en la curva, radiales (hacia el centro de cada mitad del arco).
            val juntas = Path()
            var y = s.arranque + 24f * U
            while (y < s.pretilArriba) {
                juntas.moveTo(s.huecoIzq - s.dovela, y); juntas.lineTo(s.huecoIzq, y)
                juntas.moveTo(s.huecoDer, y); juntas.lineTo(s.huecoDer + s.dovela, y)
                y += 30f * U
            }
            for (j in 1 until 9) {
                val f = Math.toRadians(180.0 + 7.5 * j)
                val cx = kotlin.math.cos(f).toFloat(); val cy = kotlin.math.sin(f).toFloat()
                juntas.moveTo(s.centroIzq.x + cx * s.radio, s.centroIzq.y + cy * s.radio); juntas.lineTo(s.centroIzq.x + cx * (s.radio + s.dovela), s.centroIzq.y + cy * (s.radio + s.dovela))
                juntas.moveTo(s.centroDer.x - cx * s.radio, s.centroDer.y + cy * s.radio); juntas.lineTo(s.centroDer.x - cx * (s.radio + s.dovela), s.centroDer.y + cy * (s.radio + s.dovela))
            }
            drawPath(juntas, a.sombra(0.45f), style = Stroke(1.2f * U))
            // Golpes y picado de la labra.
            for (j in 0 until 18) {
                val p = Offset(s.huecoIzq - s.dovela + (s.huecoDer - s.huecoIzq + 2 * s.dovela) * azar(8300, j), s.pretilArriba * azar(8301, j))
                drawCircle(a.sombra(0.25f), 0.9f * U, p)
            }
        }
        // El filo de la arista de dentro, que coge la luz a la izquierda.
        drawPath(hueco, a.sombra(0.4f), style = Stroke(1.6f * U))
        // Las impostas del arranque: una moldura que vuela.
        listOf(s.huecoIzq - s.dovela - 4f * U, s.huecoDer - 4f * U).forEachIndexed { i, x ->
            val r = Rect(x, s.arranque - 7f * U, x + s.dovela + 8f * U, s.arranque)
            val k = MuebleLibretaTemplario.cerca(r.center, s, a)
            drawRect(a.sombra(0.35f), Offset(r.left + 2f * U, r.bottom), Size(r.width, 3f * U))
            drawRect(a.dia(P.CALIZA, 0.12f, k), r.topLeft, Size(r.width, r.height * 0.45f))
            drawRect(a.dia(mezcla(P.CALIZA, P.CALIZA_OSC, 0.45f), 0f, k), Offset(r.left, r.top + r.height * 0.45f), Size(r.width, r.height * 0.55f))
            drawRect(a.sombra(0.3f), Offset(r.left, r.top + r.height * 0.45f), Size(r.width, 1f * U))
            drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.8f * U))
            if (i == 1 && a.oscuro) drawRect(a.filo(0.6f * k), r.topLeft, Size(1.4f * U, r.height))
        }
    }

    /** El fondo del hueco: la cal en sombra (más honda arriba, bajo el arco), su despiece, y la lanceta de sol o de luna. */
    private fun DrawScope.fondo(s: SitioLibretaTemplario, a: AireTemplario, hueco: Path) {
        val U = s.U
        clipPath(hueco) {
            val r = Rect(s.huecoIzq, 0f, s.huecoDer, s.pretilArriba)
            if (a.oscuro) {
                val radios = floatArrayOf(0f, 30f, 70f, 120f, 190f, 280f)
                drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 280f to a.luz(mezcla(P.CAL, P.CAL_SOMBRA, 0.5f), MuebleLibretaTemplario.cerca(Offset(s.llama.x - radios[i] * U, s.llama.y), s, a)) },
                    center = s.llama, radius = 280f * U), r.topLeft, r.size)
            } else {
                drawRect(Brush.verticalGradient(listOf(a.dia(mezcla(P.CAL_SOMBRA, P.CALIZA_OSC, 0.4f), -0.1f), a.dia(mezcla(P.CAL, P.CAL_SOMBRA, 0.7f), 0f)), 0f, s.pretilArriba), r.topLeft, r.size)
            }
            despiece(r, 0.55f, U)
            // La lanceta de la ventana: la misma cal, alumbrada (de día, entibiada; de noche, la luna, fría y más tenue).
            val l = s.lanceta(a.oscuro)
            clipPath(l) {
                if (a.oscuro) drawRect(Color(0xFF7088C6).copy(alpha = 0.56f), r.topLeft, r.size, blendMode = BlendMode.Screen)
                else {
                    drawRect(a.en(mezcla(P.CAL, P.CAL_SOMBRA, 0.35f), 0f, 1f, 1f, ManchaTemplario(FloatArray(8), P.SOL, 0.95f)), r.topLeft, r.size)
                    despiece(r, 0.6f, U)
                }
            }
            // La sombra que el arco echa dentro, arriba a la izquierda, y la del pretil abajo.
            drawRect(a.sombra(if (a.oscuro) 0.35f else 0.3f), Offset(s.huecoIzq, 0f), Size(s.huecoDer - s.huecoIzq, 18f * U))
            drawRect(a.sombra(0.18f), Offset(s.huecoIzq, 0f), Size(12f * U, s.pretilArriba))
        }
    }

    /**
     * El atril de cedro: el tablero inclinado (asoma por arriba y por los lados de la regla, con sus vetas), los dos costados
     * que lo sostienen (se ve su canto de delante, con el corte inclinado arriba), el listón donde apoya la regla, con su bocel,
     * y la sombra del listón en el pretil. La lanceta de sol (o de luna) cae también en el costado de la izquierda.
     */
    private fun DrawScope.atril(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val x0 = s.atrilIzq; val x1 = s.atrilDer; val y0 = s.atrilArriba; val y1 = s.listonArriba
        val k = MuebleLibretaTemplario.cerca(Offset(x1, (y0 + y1) / 2), s, a)
        // El tablero inclinado.
        val tab = poligonoTemplario(x0 + 8f * U, y0, x1 - 8f * U, y0, x1, y1, x0, y1)
        val n = if (a.oscuro) 8 else 1
        clipPath(tab) {
            for (t in 0 until n) {
                val xa = x0 + (x1 - x0) * t / n; val xb = x0 + (x1 - x0) * (t + 1) / n + 1.2f
                drawRect(a.dia(P.CEDRO_OSC, 0.04f, MuebleLibretaTemplario.cerca(Offset((xa + xb) / 2, (y0 + y1) / 2), s, a)), Offset(xa, y0), Size(xb - xa, y1 - y0))
            }
            // La lanceta en el tablero, que está delante del fondo (corrida otra vez).
            clipPath(s.lanceta(a.oscuro, -7f * U, -8f * U)) {
                drawRect(a.enLanceta(a.dia(P.CEDRO_OSC, 0.04f, MuebleLibretaTemplario.cerca(Offset(x0, y0), s, a)), 0.85f), Offset(x0, y0), Size(60f * U, y1 - y0))
            }
            val vetas = Path()
            for (j in 0 until 9) {
                val x = x0 + (x1 - x0) * (j + 0.5f) / 9f
                vetas.moveTo(x - 2f * U, y0); vetas.cubicTo(x + 3f * U, y0 + 40f * U, x - 3f * U, y0 + 90f * U, x + (azar(8540, j) - 0.5f) * 6f * U, y1)
            }
            drawPath(vetas, a.dia(P.CEDRO_HONDO, 0f, k).copy(alpha = 0.45f), style = Stroke(0.8f * U))
            drawRect(a.sombra(0.25f), Offset(x0, y0), Size(x1 - x0, 6f * U))
        }
        // El canto de arriba del tablero, con su luz.
        drawLine(a.dia(P.CEDRO_LUZ, 0.1f, k), Offset(x0 + 8f * U, y0), Offset(x1 - 8f * U, y0), 2f * U)
        // Los dos costados: el canto de delante de cada uno, con el corte inclinado arriba.
        listOf(true, false).forEach { izq ->
            val xa = if (izq) x0 - 10f * U else x1; val xb = if (izq) x0 else x1 + 10f * U
            val yt = y0 + 10f * U
            val c = if (izq) poligonoTemplario(xa, yt + 14f * U, xb, yt, xb, s.listonAbajo, xa, s.listonAbajo)
                else poligonoTemplario(xa, yt, xb, yt + 14f * U, xb, s.listonAbajo, xa, s.listonAbajo)
            val kk = MuebleLibretaTemplario.cerca(Offset(xa, (yt + y1) / 2), s, a)
            if (!a.oscuro && !izq) drawPath(Path().apply { addPath(c, Offset(6f * U, 4f * U)) }, a.sombra(0.25f))
            drawPath(c, a.dia(P.CEDRO, if (izq) 0.05f else -0.12f, kk))
            clipPath(c) {
                drawRect(if (izq) a.claro(0.3f) else a.sombra(0.3f), Offset(if (izq) xa else xb - 2f * U, yt), Size(2f * U, y1))
                drawLine(a.dia(P.CEDRO_HONDO, 0f, kk).copy(alpha = 0.5f), Offset((xa + xb) / 2, yt + 20f * U), Offset((xa + xb) / 2 + 1f * U, s.listonAbajo), 0.8f * U)
            }
            drawPath(c, a.sombra(0.35f), style = Stroke(0.9f * U))
            if (a.oscuro && !izq) drawRect(a.filo(0.7f * kk), Offset(xa, yt), Size(1.4f * U, s.listonAbajo - yt))
        }
        // La lanceta en el costado de la izquierda (está más adelante que el fondo: va corrida).
        clipPath(poligonoTemplario(x0 - 10f * U, y0 + 24f * U, x0, y0 + 10f * U, x0, s.listonAbajo, x0 - 10f * U, s.listonAbajo)) {
            clipPath(s.lanceta(a.oscuro, -12f * U, -10f * U)) {
                val base = a.dia(P.CEDRO, 0.05f, MuebleLibretaTemplario.cerca(Offset(x0, y0 + 60f * U), s, a))
                drawRect(a.enLanceta(base, 0.95f), Offset(x0 - 10f * U, y0), Size(10f * U, y1 - y0))
            }
        }
        // El listón donde apoya la regla: la cara de arriba, el bocel y su sombra en el pretil.
        val r = Rect(x0 - 12f * U, y1, x1 + 12f * U, s.listonAbajo)
        drawRect(a.sombra(0.4f), Offset(r.left + 3f * U, r.bottom), Size(r.width, 3.4f * U))
        val m = if (a.oscuro) 10 else 1
        for (t in 0 until m) {
            val xa = r.left + r.width * t / m; val xb = r.left + r.width * (t + 1) / m + 1.2f
            val kk = MuebleLibretaTemplario.cerca(Offset((xa + xb) / 2, r.center.y), s, a)
            drawRect(a.dia(P.CEDRO_LUZ, 0f, kk), Offset(xa, r.top), Size(minOf(xb, r.right) - xa, 3f * U))
            drawRect(a.dia(P.CEDRO, 0.05f, kk), Offset(xa, r.top + 3f * U), Size(minOf(xb, r.right) - xa, r.height - 3f * U))
        }
        drawRect(a.brillo(0.5f, k), r.topLeft, Size(r.width, 0.9f * U))
        drawRect(a.sombra(0.3f), Offset(r.left, r.top + 3f * U), Size(r.width, 0.8f * U))
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1.6f * U), Size(r.width, 1.6f * U))
        drawRect(a.claro(0.25f), Offset(r.left, r.top), Size(1.4f * U, r.height))
        drawRect(a.sombra(0.35f), Offset(r.right - 1.6f * U, r.top), Size(1.6f * U, r.height))
        if (a.oscuro) drawRect(a.filo(0.75f * k), Offset(r.left, r.top), Size(r.width, 1.2f * U))
    }

    /** El pretil de caliza del púlpito, con su moldura de arriba y la de abajo, y la ménsula que lo sostiene. */
    private fun DrawScope.pretil(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val x0 = s.pretilIzq; val x1 = s.pretilDer
        val n = if (a.oscuro) 12 else 1
        fun tramos(y0: Float, y1: Float, col: Color, f: Float) {
            for (t in 0 until n) {
                val xa = x0 + (x1 - x0) * t / n; val xb = x0 + (x1 - x0) * (t + 1) / n + 1.2f
                val kk = MuebleLibretaTemplario.cerca(Offset((xa + xb) / 2, (y0 + y1) / 2), s, a)
                drawRect(a.dia(col, f, kk), Offset(xa, y0), Size(minOf(xb, x1) - xa, y1 - y0))
            }
        }
        // La ménsula: tres molduras que se recogen hacia abajo (la última, cortada por el borde).
        val mens = listOf(Triple(s.repisaAbajo, 10f, 0.8f), Triple(s.repisaAbajo + 8f * U, 22f, 0.86f), Triple(s.repisaAbajo + 16f * U, 40f, 0.92f))
        drawPath(Path().apply { addRect(Rect(x0 + 18f * U, s.pretilAbajo, x1 + 18f * U, s.h)) }, a.sombra(0.25f))
        mens.forEach { (y, entra, _) ->
            val r = Rect(x0 + entra * U, y, x1 - entra * U, y + 8f * U)
            val kk = MuebleLibretaTemplario.cerca(r.center, s, a)
            drawRect(a.dia(P.CALIZA_OSC, 0f, kk), r.topLeft, r.size)
            drawRect(a.dia(P.CALIZA, 0f, kk), r.topLeft, Size(r.width, 3f * U))
            drawRect(a.sombra(0.4f), Offset(r.left, r.bottom - 1.4f * U), Size(r.width, 1.4f * U))
        }
        // La moldura de arriba, que vuela (la cara de encima a la luz, el bocel y su sombra).
        tramos(s.pretilArriba, s.pretilArriba + 3f * U, P.CAL_LUZ, -0.05f)
        tramos(s.pretilArriba + 3f * U, s.pretilFrente + 1f, P.CALIZA, 0.05f)
        drawRect(a.sombra(0.45f), Offset(x0, s.pretilFrente), Size(x1 - x0, 2.4f * U))
        // El frente: sillares de caliza dorada con sus juntas y su labra.
        tramos(s.pretilFrente + 1.6f * U, s.pretilAbajo + 1f, P.CALIZA, -0.05f)
        val cortes = ArrayList<Float>(); cortes.add(x0)
        var x = x0 + 30f * U; var q = 0
        while (x < x1 - 4f * U) { cortes.add(x); x += (58f + 18f * azar(8400, q)) * U; q++ }
        cortes.add(x1)
        val juntas = Path()
        for (c in 1 until cortes.size - 1) { juntas.moveTo(cortes[c], s.pretilFrente + 2f * U); juntas.lineTo(cortes[c], s.pretilAbajo) }
        // Cada sillar con su tono (unos más dorados, otros más pardos), su arista de luz arriba y su sombra abajo.
        val alto = s.pretilAbajo - s.pretilFrente - 2.4f * U
        for (c in 0 until cortes.size - 1) {
            val xa = cortes[c]; val xb = cortes[c + 1]
            val z = azar(8405, c)
            if (z < 0.35f) drawRect(a.sombra(0.1f), Offset(xa + 1f * U, s.pretilFrente + 2.4f * U), Size(xb - xa - 2f * U, alto))
            else if (z > 0.7f) drawRect(a.claro(0.12f), Offset(xa + 1f * U, s.pretilFrente + 2.4f * U), Size(xb - xa - 2f * U, alto))
            drawRect(a.claro(0.25f), Offset(xa + 1.2f * U, s.pretilFrente + 2.6f * U), Size(xb - xa - 2.4f * U, 1f * U))
            drawRect(a.sombra(0.2f), Offset(xa + 1.2f * U, s.pretilAbajo - 2.4f * U), Size(xb - xa - 2.4f * U, 2.4f * U))
        }
        drawPath(juntas, a.sombra(0.45f), style = Stroke(1.2f * U))
        // Lo rozado por las manos del lector en el borde, y una mancha de aceite de la lámpara.
        drawOval(a.sombra(0.08f), Offset(s.lampara.x - 30f * U, s.pretilFrente + 4f * U), Size(40f * U, 9f * U))
        val labra = Path()
        for (j in 0 until 40) {
            val px = x0 + (x1 - x0) * azar(8410, j); val py = s.pretilFrente + 4f * U + (s.pretilAbajo - s.pretilFrente - 8f * U) * azar(8411, j)
            labra.moveTo(px, py); labra.lineTo(px + 1.2f * U, py + 3f * U)
        }
        drawPath(labra, a.sombra(0.22f), style = Stroke(0.7f * U))
        // La moldura de abajo.
        tramos(s.pretilAbajo, s.repisaAbajo + 1f, P.CALIZA_OSC, 0f)
        drawRect(a.claro(0.3f), Offset(x0, s.pretilAbajo), Size(x1 - x0, 0.9f * U))
        drawRect(a.sombra(0.4f), Offset(x0, s.repisaAbajo - 1.4f * U), Size(x1 - x0, 1.4f * U))
        // Las testas de los lados: la de la izquierda a la luz, la de la derecha en sombra.
        drawRect(a.claro(0.2f), Offset(x0, s.pretilArriba), Size(1.6f * U, s.repisaAbajo - s.pretilArriba))
        drawRect(a.sombra(0.3f), Offset(x1 - 2f * U, s.pretilArriba), Size(2f * U, s.repisaAbajo - s.pretilArriba))
    }

    /** El pelo de la lana: trazos cortos y apretados, más oscuros (sin degradados). */
    private fun DrawScope.pinceladasLana(r: Rect, a: AireTemplario) {
        val p = Path()
        for (j in 0 until 70) {
            val x = r.left + r.width * azar(8530, j); val y = r.top + r.height * azar(8531, j)
            p.moveTo(x, y); p.lineTo(x + 0.8f, y + 2.4f * (r.height / 34f))
        }
        drawPath(p, a.sombra(0.18f), style = Stroke(0.8f))
    }

    /** El paño verde del atril, que cuelga del canto por delante del pretil: su orla de grana, los pliegues y los flecos. */
    private fun DrawScope.pano(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val x0 = l.left + l.width * 0.12f; val x1 = l.right - l.width * 0.12f
        val y0 = s.listonAbajo - 1f * U; val y1 = s.pretilAbajo - 6f * U
        val forma = Path().apply {
            moveTo(x0, y0); lineTo(x1, y0)
            cubicTo(x1 + 2f * U, y0 + 10f * U, x1 - 2f * U, y1 - 8f * U, x1 + 1f * U, y1)
            // El bajo, ondulado donde caen los pliegues (sin dientes iguales).
            var x = x1
            var j = 0
            while (x > x0) { x -= (7f + 9f * azar(8526, j)) * U; lineTo(maxOf(x, x0), y1 + (3.6f * azar(8527, j) - 0.6f) * U); j++ }
            cubicTo(x0 - 2f * U, y1 - 8f * U, x0 + 2f * U, y0 + 10f * U, x0, y0)
            close()
        }
        drawPath(Path().apply { addPath(forma, Offset(4f * U, 3f * U)) }, a.sombra(0.35f))
        val n = if (a.oscuro) 8 else 1
        clipPath(forma) {
            for (t in 0 until n) {
                val xa = x0 + (x1 - x0) * t / n - 2f * U; val xb = x0 + (x1 - x0) * (t + 1) / n + 2f * U
                drawRect(a.dia(PANO, 0f, MuebleLibretaTemplario.cerca(Offset((xa + xb) / 2, (y0 + y1) / 2), s, a)), Offset(xa, y0), Size(xb - xa, y1 - y0 + 6f * U))
            }
            // Los pliegues: nacen apretados bajo el listón y se abren hacia abajo, a trechos desiguales, unos largos y otros
            // que se pierden a media caída, ladeados cada uno a su aire; el hondo curvo y, a su izquierda, la cresta de luz.
            val hondos = Path(); val crestas = Path(); val nucleos = Path()
            var xf = x0 + (5f + 9f * azar(8520, 0)) * U
            var q = 0
            while (xf < x1 - 6f * U) {
                val an = (2.5f + 6.5f * azar(8521, q)) * U
                val largo = (0.45f + 0.6f * azar(8522, q)).coerceAtMost(1f) * (y1 + 5f * U - y0)
                val ladeo = (azar(8523, q) - 0.5f) * 9f * U
                val yb = y0 + largo
                hondos.moveTo(xf, y0)
                hondos.cubicTo(xf + an * 0.25f, y0 + largo * 0.35f, xf + ladeo + an * 0.75f, yb - largo * 0.3f, xf + ladeo + an, yb)
                hondos.lineTo(xf + ladeo - an * 0.35f, yb + 1f * U)
                hondos.cubicTo(xf + ladeo - an * 0.3f, yb - largo * 0.35f, xf - an * 0.1f, y0 + largo * 0.4f, xf, y0)
                hondos.close()
                nucleos.moveTo(xf + 0.3f * U, y0 + 1f * U)
                nucleos.quadraticTo(xf + ladeo * 0.3f + an * 0.2f, y0 + largo * 0.5f, xf + ladeo + an * 0.25f, yb - 1f * U)
                crestas.moveTo(xf - 2.2f * U, y0 + 1.5f * U)
                crestas.cubicTo(xf - 2.6f * U, y0 + largo * 0.4f, xf + ladeo - an * 0.5f - 2f * U, yb - largo * 0.3f, xf + ladeo - an * 0.7f - 1.8f * U, yb - 0.5f * U)
                xf += (11f + 21f * azar(8524, q)) * U; q++
            }
            drawPath(hondos, a.sombra(0.26f))
            drawPath(nucleos, a.sombra(0.3f), style = Stroke(1f * U, cap = StrokeCap.Round))
            drawPath(crestas, if (a.oscuro) a.brillo(0.22f, MuebleLibretaTemplario.cerca(Offset(x1, y0), s, a)) else a.claro(0.26f), style = Stroke(1.5f * U, cap = StrokeCap.Round))
            pinceladasLana(Rect(x0, y0, x1, y1), a)
            // La orla de grana de abajo, con su filete, y el pelo de la lana.
            val kk = MuebleLibretaTemplario.cerca(Offset(s.cx, y1), s, a)
            drawRect(a.dia(PANO_ORLA, 0f, kk), Offset(x0 - 4f * U, y1 - 9f * U), Size(x1 - x0 + 8f * U, 6f * U))
            drawRect(a.dia(P.LATON, -0.1f, kk), Offset(x0 - 4f * U, y1 - 10.5f * U), Size(x1 - x0 + 8f * U, 1.2f * U))
            drawRect(a.sombra(0.35f), Offset(x0, y0), Size(x1 - x0, 3f * U))
        }
        // Los flecos.
        val flecos = Path()
        var x = x0 + 2f * U
        while (x < x1 - 1f * U) { flecos.moveTo(x, y1); flecos.lineTo(x + 0.6f * U, y1 + (4f + 2f * azar(8500, (x / U).toInt())) * U); x += 2.6f * U }
        drawPath(flecos, a.dia(PANO_ORLA, -0.1f, MuebleLibretaTemplario.cerca(Offset(s.cx, y1), s, a)), style = Stroke(0.9f * U, cap = StrokeCap.Round))
    }
}
