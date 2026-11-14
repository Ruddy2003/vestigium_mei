package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRenacimiento.Motivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las pinturas del taller: las tres tablas del maestro, la Virgen pequeña de Iacopo, el santo de las tablas que
 * esperan y los dibujos de los cartones.
 *
 * **[Diseño propio]**: son inventadas. No copian ninguna obra ni la manera de ningún pintor: son figuras de manto y
 * túnica, con su toca o su pelo, sus manos y su nimbo, delante de unas colinas o de un fondo de oro, sin letras.
 *
 * Cada tabla se pinta por pasos, como dice el cuento que se hace en el taller: sobre la madera enyesada va el dibujo
 * a carbón, en línea fina; después el oro de los nimbos, del fondo y del marco; después los cielos, los suelos y las
 * arquitecturas; después las túnicas y los mantos, y al final las carnes («las caras son lo último») y los toques de
 * luz. [avance], de 0 a 1, dice por dónde va: cada zona se pinta al llegar a su umbral; hasta entonces se ve su dibujo
 * (el contorno y las líneas de dentro: pliegues, rasgos) y, cuando le falta poco, su primera mano, de tierra verde.
 * **[Aproximación]**: el orden de verdad dependía de cada taller y de cada tabla.
 */
internal object PinturasRenacimiento {
    private val C = ColorRenacimiento

    internal const val DIBUJO = 0.08f
    internal const val DORADO = 0.2f
    internal const val FONDO = 0.3f
    internal const val SUELO = 0.42f
    internal const val MANTO_1 = 0.55f
    internal const val MANTO_2 = 0.68f
    internal const val CARNES = 0.82f
    internal const val LUCES = 0.94f

    /** Una tabla dentro de su caja: lleva (u, v), de 0 a 1, a un punto del cuadro, y da a cada color la luz del sitio. */
    private class Lienzo(val caja: Rect, val tono: (Color) -> Color) {
        fun p(u: Float, v: Float) = Offset(caja.left + u * caja.width, caja.top + v * caja.height)
        /** Una medida de la tabla (0 a 1 de su ancho) en unidades del cuadro. */
        fun r(k: Float) = k * caja.width
        /** Cuánto es, a lo alto de la tabla, una medida dada a lo ancho. */
        fun dv(du: Float) = du * caja.width / caja.height
        /** El grueso de una línea de dibujo. */
        val fino: Float get() = r(0.009f).coerceIn(0.4f, 0.9f)
    }

    /**
     * Una zona de una pintura: cuándo se pinta ([umbral]), su silueta, su color, lo que lleva pintado dentro ([relleno])
     * y sus líneas ([lineas]: pliegues, rasgos), que se ven ya en el dibujo, a carbón, y después, en el tono oscuro del
     * color. [dibujoDesde]: desde cuándo está dibujada.
     */
    private class Zona(val umbral: Float, val forma: Path, val color: Color, val dibujoDesde: Float = DIBUJO, val relleno: (DrawScope.() -> Unit)? = null, val lineas: (DrawScope.(Color) -> Unit)? = null)

    private fun DrawScope.pintar(L: Lienzo, zonas: List<Zona>, avance: Float) {
        // La madera enyesada: blanca, con las pasadas de la brocha.
        drawRect(L.tono(C.YESO), L.caja.topLeft, L.caja.size)
        for (k in 1..5) drawLine(L.tono(lerp(C.YESO, C.PIEDRA, 0.25f)).copy(alpha = 0.3f), L.p(0f, k / 6f), L.p(1f, k / 6f + 0.02f), 0.5f)
        val carbon = L.tono(lerp(C.CARBON, C.VIGA, 0.35f))
        clipPath(Path().apply { addRect(L.caja) }) {
            for (z in zonas) {
                if (avance >= z.umbral) {
                    drawPath(z.forma, L.tono(z.color))
                    clipPath(z.forma) { z.relleno?.invoke(this); z.lineas?.invoke(this, L.tono(lerp(z.color, C.CARBON, 0.5f)).copy(alpha = 0.7f)) }
                } else if (avance >= z.dibujoDesde) {
                    // El yeso tapa lo que hay debajo; encima, la primera mano (si le falta poco) y el dibujo, en línea fina.
                    drawPath(z.forma, L.tono(C.YESO).copy(alpha = 0.8f))
                    // La primera mano: plana, sin modelar. En las carnes, la tierra verde de debajo; en lo demás, el color aguado.
                    val falta = z.umbral - avance
                    if (falta <= 0.16f) drawPath(z.forma, L.tono(if (z.umbral == CARNES) lerp(z.color, lerp(C.VERDE, C.OCRE, 0.4f), 0.6f) else lerp(z.color, C.YESO, 0.16f)).copy(alpha = 0.34f + 0.4f * (1f - falta / 0.16f)))
                    clipPath(z.forma) { z.lineas?.invoke(this, carbon.copy(alpha = 0.62f)) }
                    drawPath(z.forma, carbon.copy(alpha = 0.7f), style = Stroke(L.fino))
                }
            }
        }
    }

    private fun circulo(L: Lienzo, u: Float, v: Float, r: Float): Path = Path().apply { val o = L.p(u, v); addOval(Rect(o.x - L.r(r), o.y - L.r(r), o.x + L.r(r), o.y + L.r(r))) }

    private fun junta(vararg formas: Path): Path = Path().apply { for (f in formas) addPath(f) }

    private fun trazo(L: Lienzo, vararg uv: Float): Path = Path().apply { moveTo(L.p(uv[0], uv[1])); var i = 2; while (i + 5 < uv.size + 0) { cubicTo(L.p(uv[i], uv[i + 1]), L.p(uv[i + 2], uv[i + 3]), L.p(uv[i + 4], uv[i + 5])); i += 6 } }

    /**
     * Una figura de pie, o de rodillas si [rodillas]: el nimbo, la túnica con su ceñidor, el manto abierto por delante,
     * la toca (o el pelo) y las carnes, con las manos donde diga [manos]. Devuelve sus zonas, de atrás adelante.
     * [cx] y [cab]: el centro de la cabeza. [pie]: hasta dónde llega. [ancho]: el de los hombros. [mira]: de −1 (a la
     * izquierda) a 1. [a]: el avance de la tabla, para los toques de luz. [desde]: desde cuándo está dibujada.
     */
    private fun figura(
        L: Lienzo, cx: Float, cab: Float, pie: Float, ancho: Float, manto: Color, tunica: Color, toca: Color?, pelo: Color, mira: Float, manos: List<Offset>, a: Float,
        nimbo: Boolean = true, uTunica: Float = MANTO_1, uManto: Float = MANTO_2, rodillas: Boolean = false, desde: Float = DIBUJO, barba: Boolean = false,
    ): List<Zona> = buildList {
        val hr = ancho * 0.2f; val hom = cab + L.dv(hr * 1.45f); val alto = pie - hom; val luces = a >= LUCES
        val abre = if (rodillas) 1.5f else 1f
        if (nimbo) add(Zona(DORADO, circulo(L, cx, cab, hr * 1.95f), C.ORO, desde, relleno = {
            val o = L.p(cx, cab)
            drawCircle(L.tono(lerp(C.ORO, C.YESO, 0.4f)), L.r(hr) * 1.6f, o, style = Stroke(L.r(hr) * 0.16f))
            if (luces) for (k in 0 until 12) { val t = 6.2832f * k / 12f; drawCircle(L.tono(lerp(C.ORO, C.VIGA, 0.45f)), L.r(hr) * 0.09f, o + Offset(cos(t), sin(t)) * (L.r(hr) * 1.24f)) }
        }))
        // La túnica: del cuello al suelo, ceñida.
        add(Zona(uTunica, Path().apply {
            moveTo(L.p(cx - ancho * 0.36f, hom + alto * 0.03f)); cubicTo(L.p(cx - ancho * 0.2f, hom - alto * 0.03f), L.p(cx + ancho * 0.2f, hom - alto * 0.03f), L.p(cx + ancho * 0.36f, hom + alto * 0.03f))
            cubicTo(L.p(cx + ancho * 0.4f, hom + alto * 0.4f), L.p(cx + ancho * 0.44f * abre, pie - alto * 0.1f), L.p(cx + ancho * 0.48f * abre, pie)); lineTo(L.p(cx - ancho * 0.48f * abre, pie))
            cubicTo(L.p(cx - ancho * 0.44f * abre, pie - alto * 0.1f), L.p(cx - ancho * 0.4f, hom + alto * 0.4f), L.p(cx - ancho * 0.36f, hom + alto * 0.03f)); close()
        }, tunica, desde, relleno = {
            // La luz le da por el lado de la ventana; el otro, en sombra.
            drawRect(Brush.horizontalGradient(0f to L.tono(lerp(tunica, C.YESO, 0.3f)).copy(alpha = 0.7f), 0.5f to Color.Transparent, 1f to L.tono(lerp(tunica, C.CARBON, 0.4f)).copy(alpha = 0.6f), startX = L.p(cx - ancho * 0.4f, 0f).x, endX = L.p(cx + ancho * 0.4f, 0f).x), L.caja.topLeft, L.caja.size)
            drawLine(L.tono(C.ORO), L.p(cx - ancho * 0.4f, hom + alto * 0.36f), L.p(cx + ancho * 0.4f, hom + alto * 0.35f), L.r(0.014f).coerceAtLeast(0.6f))
        }, lineas = { c ->
            // El escote, el ceñidor y los pliegues, que caen rectos de la cintura.
            drawPath(trazo(L, cx - ancho * 0.14f, hom, cx - ancho * 0.06f, hom + alto * 0.06f, cx + ancho * 0.06f, hom + alto * 0.06f, cx + ancho * 0.14f, hom), c, style = Stroke(L.fino))
            drawLine(c, L.p(cx - ancho * 0.4f, hom + alto * 0.37f), L.p(cx + ancho * 0.4f, hom + alto * 0.36f), L.fino)
            for (k in listOf(-0.2f, -0.05f, 0.1f, 0.24f)) drawPath(trazo(L, cx + ancho * k, hom + alto * 0.4f, cx + ancho * k * 1.1f, hom + alto * 0.6f, cx + ancho * k * 1.3f * abre, pie - alto * 0.14f, cx + ancho * k * 1.4f * abre, pie), c, style = Stroke(L.fino, cap = StrokeCap.Round))
        }))
        // El manto: dos paños que caen de los hombros y dejan ver la túnica por delante.
        fun pano(k: Float): Path = Path().apply {
            moveTo(L.p(cx + k * ancho * 0.1f, hom - alto * 0.01f)); cubicTo(L.p(cx + k * ancho * 0.3f, hom - alto * 0.04f), L.p(cx + k * ancho * 0.48f, hom + alto * 0.02f), L.p(cx + k * ancho * 0.52f, hom + alto * 0.14f))
            cubicTo(L.p(cx + k * ancho * 0.6f, hom + alto * 0.5f), L.p(cx + k * ancho * 0.68f * abre, pie - alto * 0.12f), L.p(cx + k * ancho * 0.78f * abre, pie)); lineTo(L.p(cx + k * ancho * 0.3f * abre, pie))
            cubicTo(L.p(cx + k * ancho * 0.26f, pie - alto * 0.3f), L.p(cx + k * ancho * 0.34f, hom + alto * 0.5f), L.p(cx + k * ancho * 0.22f, hom + alto * 0.3f)); cubicTo(L.p(cx + k * ancho * 0.16f, hom + alto * 0.2f), L.p(cx + k * ancho * 0.12f, hom + alto * 0.1f), L.p(cx + k * ancho * 0.1f, hom - alto * 0.01f)); close()
        }
        add(Zona(uManto, junta(pano(-1f), pano(1f)), manto, desde, relleno = {
            drawRect(Brush.horizontalGradient(0f to L.tono(lerp(manto, C.YESO, 0.34f)).copy(alpha = 0.8f), 0.45f to Color.Transparent, 1f to L.tono(lerp(manto, C.CARBON, 0.45f)).copy(alpha = 0.7f), startX = L.p(cx - ancho * 0.7f, 0f).x, endX = L.p(cx + ancho * 0.7f, 0f).x), L.caja.topLeft, L.caja.size)
            // El forro, que asoma en el borde de dentro, y la cenefa de oro del borde de abajo.
            for (k in listOf(-1f, 1f)) drawPath(trazo(L, cx + k * ancho * 0.12f, hom + alto * 0.08f, cx + k * ancho * 0.2f, hom + alto * 0.26f, cx + k * ancho * 0.34f, hom + alto * 0.5f, cx + k * ancho * 0.28f * abre, pie), L.tono(lerp(manto, C.YESO, 0.55f)), style = Stroke(L.r(0.016f).coerceAtLeast(0.7f)))
            if (luces) for (k in listOf(-1f, 1f)) drawLine(L.tono(C.ORO), L.p(cx + k * ancho * 0.3f * abre, pie - alto * 0.012f), L.p(cx + k * ancho * 0.78f * abre, pie - alto * 0.012f), L.r(0.014f).coerceAtLeast(0.6f))
        }, lineas = { c ->
            for (k in listOf(-1f, 1f)) for (j in listOf(0.34f, 0.46f, 0.58f)) drawPath(trazo(L, cx + k * ancho * (j - 0.06f), hom + alto * 0.1f, cx + k * ancho * j, hom + alto * 0.4f, cx + k * ancho * (j + 0.06f) * abre, pie - alto * 0.2f, cx + k * ancho * (j + 0.1f) * abre, pie), c, style = Stroke(L.fino, cap = StrokeCap.Round))
        }))
        // La toca, que cubre la cabeza y cae a los hombros.
        if (toca != null) add(Zona(uManto, Path().apply {
            moveTo(L.p(cx, cab - L.dv(hr * 1.2f))); cubicTo(L.p(cx + hr * 1.3f, cab - L.dv(hr * 1.2f)), L.p(cx + hr * 1.3f, cab + L.dv(hr * 0.6f)), L.p(cx + hr * 1.7f, hom + alto * 0.05f))
            lineTo(L.p(cx - hr * 1.7f, hom + alto * 0.05f)); cubicTo(L.p(cx - hr * 1.3f, cab + L.dv(hr * 0.6f)), L.p(cx - hr * 1.3f, cab - L.dv(hr * 1.2f)), L.p(cx, cab - L.dv(hr * 1.2f))); close()
        }, toca, desde, lineas = { c -> for (k in listOf(-1f, 1f)) drawPath(trazo(L, cx + k * hr * 0.9f, cab - L.dv(hr * 0.6f), cx + k * hr * 1.2f, cab, cx + k * hr * 1.2f, cab + L.dv(hr * 0.8f), cx + k * hr * 1.5f, hom + alto * 0.04f), c, style = Stroke(L.fino)) }))
        // Las carnes: el cuello, la cara y las manos. Encima, el pelo (si no lleva toca) y los rasgos.
        val cara = Path().apply { val o = L.p(cx + mira * hr * 0.1f, cab); addOval(Rect(o.x - L.r(hr) * 0.82f, o.y - L.r(hr), o.x + L.r(hr) * 0.82f, o.y + L.r(hr))) }
        val cuello = poligono(L.p(cx - hr * 0.36f, cab + L.dv(hr * 0.7f)), L.p(cx + hr * 0.36f, cab + L.dv(hr * 0.7f)), L.p(cx + hr * 0.5f, hom + alto * 0.02f), L.p(cx - hr * 0.5f, hom + alto * 0.02f))
        val formaManos = manos.map { Path().apply { val o = L.p(it.x, it.y); addOval(Rect(o.x - L.r(hr) * 0.5f, o.y - L.r(hr) * 0.34f, o.x + L.r(hr) * 0.5f, o.y + L.r(hr) * 0.34f)) } }
        add(Zona(CARNES, junta(cuello, cara, *formaManos.toTypedArray()), C.CARNE, desde, relleno = {
            val o = L.p(cx + mira * hr * 0.1f, cab); val rr = L.r(hr)
            // La mejilla del lado de la sombra y un poco de color en la otra.
            drawOval(L.tono(lerp(C.CARNE, C.VIGA, 0.34f)).copy(alpha = 0.55f), Offset(o.x - rr * (if (mira >= 0f) 0.9f else -0.1f), o.y - rr * 0.9f), Size(rr * 0.8f, rr * 1.9f))
            drawCircle(L.tono(lerp(C.CARNE, C.CINABRIO, 0.4f)).copy(alpha = 0.5f), rr * 0.24f, o + Offset(rr * (0.3f * mira + 0.14f), rr * 0.26f))
            if (toca == null) {
                // El pelo: un casquete que baja por la nuca; con barba, también por la mandíbula.
                drawPath(Path().apply { moveTo(o.x - rr * 0.9f, o.y + rr * 0.1f); cubicTo(o.x - rr * 1.1f, o.y - rr * 1.3f, o.x + rr * 1.1f, o.y - rr * 1.3f, o.x + rr * 0.9f, o.y + rr * 0.1f); cubicTo(o.x + rr * 0.5f, o.y - rr * 0.7f, o.x - rr * 0.5f, o.y - rr * 0.7f, o.x - rr * 0.9f, o.y + rr * 0.1f); close() }, L.tono(pelo))
                drawRect(L.tono(pelo), Offset(o.x - rr * (if (mira >= 0f) 0.9f else -0.56f), o.y - rr * 0.3f), Size(rr * 0.34f, rr * 1.1f))
                if (barba) drawPath(Path().apply { moveTo(o.x - rr * 0.7f, o.y + rr * 0.3f); cubicTo(o.x - rr * 0.6f, o.y + rr * 1.5f, o.x + rr * 0.6f, o.y + rr * 1.5f, o.x + rr * 0.7f, o.y + rr * 0.3f); cubicTo(o.x + rr * 0.3f, o.y + rr * 0.7f, o.x - rr * 0.3f, o.y + rr * 0.7f, o.x - rr * 0.7f, o.y + rr * 0.3f); close() }, L.tono(pelo))
            }
            if (luces) drawCircle(L.tono(C.YESO).copy(alpha = 0.6f), rr * 0.16f, o + Offset(rr * (0.2f * mira - 0.2f), -rr * 0.44f))
        }, lineas = { c ->
            val o = L.p(cx + mira * hr * 0.1f, cab); val rr = L.r(hr); val g = (rr * 0.13f).coerceAtLeast(0.35f)
            // Las cejas, los ojos, la nariz y la boca.
            for (k in listOf(-0.36f, 0.36f)) { val x = o.x + rr * (k + mira * 0.22f); drawLine(c, Offset(x - rr * 0.2f, o.y - rr * 0.26f), Offset(x + rr * 0.2f, o.y - rr * 0.28f), g * 0.7f); drawCircle(c, g * 0.8f, Offset(x, o.y - rr * 0.08f)) }
            drawLine(c, o + Offset(rr * mira * 0.24f, -rr * 0.06f), o + Offset(rr * mira * 0.34f, rr * 0.3f), g * 0.7f)
            drawLine(c, o + Offset(rr * (mira * 0.24f - 0.2f), rr * 0.54f), o + Offset(rr * (mira * 0.24f + 0.2f), rr * 0.54f), g)
            for (it in manos) { val q = L.p(it.x, it.y); for (k in listOf(-0.2f, 0f, 0.2f)) drawLine(c, q + Offset(-rr * 0.36f * mira, rr * k), q + Offset(rr * 0.4f * mira, rr * k * 0.8f), g * 0.5f) }
        }))
    }

    /** El cielo de las tablas: azul arriba, casi blanco en el horizonte, con dos nubes. */
    private fun cielo(L: Lienzo, hasta: Float) = Zona(FONDO, poligono(L.p(0f, 0f), L.p(1f, 0f), L.p(1f, hasta), L.p(0f, hasta)), lerp(C.ULTRAMAR, C.YESO, 0.42f), relleno = {
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to L.tono(C.YESO).copy(alpha = 0.9f), startY = L.p(0f, hasta * 0.2f).y, endY = L.p(0f, hasta).y), L.caja.topLeft, Size(L.caja.width, L.caja.height * hasta))
        for ((u, v, w) in listOf(Triple(0.08f, 0.1f, 0.2f), Triple(0.7f, 0.16f, 0.16f), Triple(0.42f, 0.06f, 0.12f))) { drawOval(L.tono(C.YESO).copy(alpha = 0.85f), L.p(u, v), Size(L.r(w), L.r(w * 0.28f))); drawOval(L.tono(C.YESO).copy(alpha = 0.85f), L.p(u + w * 0.2f, v - L.dv(w * 0.1f)), Size(L.r(w * 0.5f), L.r(w * 0.3f))) }
    }, lineas = { c -> drawLine(c.copy(alpha = c.alpha * 0.5f), L.p(0f, hasta * 0.98f), L.p(1f, hasta * 0.98f), L.fino * 0.7f) })

    /** Unas colinas con cipreses, un río y un pueblo con su torre, del [v0] al [v1] de la tabla. */
    private fun colinas(L: Lienzo, v0: Float, v1: Float) = Zona(FONDO, Path().apply { moveTo(L.p(0f, v1)); lineTo(L.p(0f, v0 + 0.05f)); cubicTo(L.p(0.2f, v0 - 0.04f), L.p(0.34f, v0 + 0.04f), L.p(0.5f, v0 + 0.03f)); cubicTo(L.p(0.66f, v0 + 0.02f), L.p(0.8f, v0 - 0.05f), L.p(1f, v0 + 0.02f)); lineTo(L.p(1f, v1)); close() }, lerp(C.VERDE, C.OCRE, 0.25f), relleno = {
        // La loma de lejos, azulada; la de cerca, con sus campos; y el río, que serpea.
        drawPath(Path().apply { moveTo(L.p(0f, v0 + 0.06f)); cubicTo(L.p(0.2f, v0 - 0.04f), L.p(0.34f, v0 + 0.04f), L.p(0.5f, v0 + 0.03f)); cubicTo(L.p(0.66f, v0 + 0.02f), L.p(0.8f, v0 - 0.05f), L.p(1f, v0 + 0.02f)); lineTo(L.p(1f, v0 + 0.07f)); lineTo(L.p(0f, v0 + 0.1f)); close() }, L.tono(lerp(C.VERDE, C.ULTRAMAR, 0.4f)).copy(alpha = 0.6f))
        drawPath(Path().apply { moveTo(L.p(0f, v1)); lineTo(L.p(0f, v0 + 0.12f)); cubicTo(L.p(0.3f, v0 + 0.07f), L.p(0.5f, v0 + 0.14f), L.p(1f, v0 + 0.1f)); lineTo(L.p(1f, v1)); close() }, L.tono(lerp(C.VERDE, C.CARBON, 0.18f)).copy(alpha = 0.7f))
        drawPath(trazo(L, 0.02f, v0 + 0.1f, 0.2f, v0 + 0.08f, 0.26f, v0 + 0.13f, 0.44f, v0 + 0.12f), L.tono(lerp(C.ULTRAMAR, C.YESO, 0.6f)), style = Stroke(L.r(0.016f).coerceAtLeast(0.6f), cap = StrokeCap.Round))
        for (u in listOf(0.06f, 0.1f, 0.14f, 0.84f, 0.9f, 0.95f)) drawPath(poligono(L.p(u - 0.012f, v0 + 0.09f), L.p(u, v0 - 0.03f + 0.02f * ((u * 50f) % 2f)), L.p(u + 0.012f, v0 + 0.09f)), L.tono(lerp(C.VERDE, C.CARBON, 0.6f)))
        // El pueblo: unas casas con tejado y su torre.
        for ((k, u) in listOf(0.62f, 0.67f, 0.71f).withIndex()) { drawRect(L.tono(lerp(C.MURO, C.YESO, 0.3f)), L.p(u, v0 + 0.01f), Size(L.r(0.045f), L.r(0.04f + 0.012f * k))); drawRect(L.tono(C.TEJA), L.p(u - 0.004f, v0 + 0.005f), Size(L.r(0.053f), L.r(0.012f))) }
        drawRect(L.tono(C.MURO), L.p(0.76f, v0 - 0.04f), Size(L.r(0.034f), L.r(0.1f))); drawPath(poligono(L.p(0.752f, v0 - 0.04f), L.p(0.802f, v0 - 0.04f), L.p(0.777f, v0 - 0.075f)), L.tono(C.TEJA))
    }, lineas = { c -> drawPath(trazo(L, 0f, v0 + 0.12f, 0.3f, v0 + 0.07f, 0.5f, v0 + 0.14f, 1f, v0 + 0.1f), c.copy(alpha = c.alpha * 0.6f), style = Stroke(L.fino * 0.8f)) })

    /** El suelo de tierra de las tablas, con un camino, piedras y matas en flor. */
    private fun suelo(L: Lienzo, desde: Float) = Zona(SUELO, poligono(L.p(0f, desde), L.p(1f, desde), L.p(1f, 1f), L.p(0f, 1f)), lerp(C.OCRE, C.VERDE, 0.4f), relleno = {
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to L.tono(lerp(C.VERDE, C.CARBON, 0.3f)).copy(alpha = 0.5f), startY = L.p(0f, desde).y, endY = L.caja.bottom), L.p(0f, desde), Size(L.caja.width, L.caja.height * (1f - desde)))
        drawPath(Path().apply { moveTo(L.p(0.36f, 1f)); cubicTo(L.p(0.42f, 0.9f), L.p(0.6f, desde + 0.06f), L.p(0.5f, desde)); lineTo(L.p(0.58f, desde)); cubicTo(L.p(0.72f, desde + 0.08f), L.p(0.6f, 0.92f), L.p(0.66f, 1f)); close() }, L.tono(lerp(C.MURO, C.YESO, 0.3f)).copy(alpha = 0.8f))
        val az = Azar(Semilla.de("renacimiento", "prado", L.caja.left.toInt()))
        repeat(16) { val u = az.entero(1000) / 1000f; val v = desde + 0.03f + (1f - desde - 0.04f) * (az.entero(1000) / 1000f); if (u < 0.36f || u > 0.66f) { drawLine(L.tono(lerp(C.VERDE, C.CARBON, 0.35f)), L.p(u, v), L.p(u + 0.008f, v - 0.03f), L.r(0.01f), StrokeCap.Round); if (it % 3 == 0) drawCircle(L.tono(if (it % 2 == 0) C.YESO else C.CINABRIO), L.r(0.01f), L.p(u + 0.008f, v - 0.034f)) } }
    }, lineas = { c -> drawPath(trazo(L, 0.36f, 1f, 0.42f, 0.9f, 0.6f, desde + 0.06f, 0.5f, desde), c.copy(alpha = c.alpha * 0.6f), style = Stroke(L.fino * 0.8f)); drawPath(trazo(L, 0.66f, 1f, 0.6f, 0.92f, 0.72f, desde + 0.08f, 0.58f, desde), c.copy(alpha = c.alpha * 0.6f), style = Stroke(L.fino * 0.8f)) })

    // ---------------------------------------------------------------- Las tres tablas del maestro

    /** El encuentro: dos mujeres de manto se dan las manos en un camino, delante de una puerta de piedra y de unas colinas. */
    private fun encuentro(L: Lienzo, a: Float): List<Zona> = buildList {
        add(cielo(L, 0.58f)); add(colinas(L, 0.47f, 0.7f)); add(suelo(L, 0.68f))
        // La puerta de la casa, a la derecha: una jamba, su arco y la sombra de dentro.
        add(Zona(SUELO, Path().apply { moveTo(L.p(0.8f, 0.86f)); lineTo(L.p(0.8f, 0.3f)); cubicTo(L.p(0.8f, 0.12f), L.p(1f, 0.1f), L.p(1f, 0.1f)); lineTo(L.p(1f, 0.86f)); close() }, lerp(C.PIEDRA, C.MURO, 0.5f), relleno = {
            drawPath(Path().apply { moveTo(L.p(0.87f, 0.86f)); lineTo(L.p(0.87f, 0.36f)); cubicTo(L.p(0.87f, 0.22f), L.p(1f, 0.2f), L.p(1f, 0.2f)); lineTo(L.p(1f, 0.86f)); close() }, L.tono(lerp(C.VIGA, C.CARBON, 0.3f)))
            for (v in listOf(0.4f, 0.52f, 0.64f, 0.76f)) drawLine(L.tono(lerp(C.PIEDRA, C.CARBON, 0.3f)).copy(alpha = 0.5f), L.p(0.8f, v), L.p(0.87f, v), L.fino * 0.7f)
        }, lineas = { c -> drawPath(trazo(L, 0.87f, 0.36f, 0.87f, 0.22f, 1f, 0.2f, 1f, 0.2f), c, style = Stroke(L.fino)); drawLine(c, L.p(0.87f, 0.36f), L.p(0.87f, 0.86f), L.fino) }))
        addAll(figura(L, 0.3f, 0.27f, 0.95f, 0.36f, C.ULTRAMAR, C.CINABRIO, C.YESO, C.VIGA, 1f, listOf(Offset(0.455f, 0.52f)), a))
        addAll(figura(L, 0.63f, 0.29f, 0.95f, 0.36f, lerp(C.OCRE, C.CINABRIO, 0.25f), C.VERDE, lerp(C.YESO, C.OCRE, 0.3f), C.VIGA, -1f, listOf(Offset(0.5f, 0.53f)), a, uTunica = MANTO_2, uManto = MANTO_1))
    }

    /** Los pastores: a la izquierda, un pastor con un cordero en brazos y su cayado; a la derecha, una mujer de rodillas; entre los dos, un niño en la paja, bajo un cobertizo. */
    private fun pastores(L: Lienzo, a: Float): List<Zona> = buildList {
        add(cielo(L, 0.56f)); add(colinas(L, 0.44f, 0.68f)); add(suelo(L, 0.66f))
        // El cobertizo: dos palos, la viga y un techo de paja.
        add(Zona(SUELO, junta(poligono(L.p(0.4f, 0.17f), L.p(1f, 0.08f), L.p(1f, 0.2f), L.p(0.44f, 0.29f)), poligono(L.p(0.47f, 0.27f), L.p(0.505f, 0.27f), L.p(0.505f, 0.72f), L.p(0.47f, 0.72f)), poligono(L.p(0.93f, 0.19f), L.p(0.965f, 0.19f), L.p(0.965f, 0.74f), L.p(0.93f, 0.74f))), lerp(C.OCRE, C.VIGA, 0.4f), relleno = {
            for (k in 0..9) drawLine(L.tono(lerp(C.OCRE, C.YESO, 0.35f)).copy(alpha = 0.7f), L.p(0.42f + k * 0.062f, 0.21f - k * 0.013f), L.p(0.445f + k * 0.062f, 0.29f - k * 0.013f), L.r(0.012f))
            drawLine(L.tono(lerp(C.VIGA, C.CARBON, 0.3f)), L.p(0.44f, 0.29f), L.p(1f, 0.2f), L.r(0.014f))
        }, lineas = { c -> drawLine(c, L.p(0.44f, 0.29f), L.p(1f, 0.2f), L.fino) }))
        // El pastor: no se dibuja hasta que Iacopo posa. Lleva zamarra parda sobre un sayo ocre, y la cabeza descubierta.
        add(Zona(MANTO_1, poligono(L.p(0.055f, 0.14f), L.p(0.08f, 0.14f), L.p(0.1f, 0.96f), L.p(0.078f, 0.96f)), lerp(C.MADERA, C.VIGA, 0.4f), dibujoDesde = 0.41f))
        addAll(figura(L, 0.23f, 0.27f, 0.96f, 0.34f, lerp(C.VIGA, C.MADERA, 0.35f), lerp(C.OCRE, C.MURO, 0.3f), null, lerp(C.VIGA, C.CARBON, 0.3f), 1f, listOf(Offset(0.36f, 0.5f), Offset(0.15f, 0.54f)), a, nimbo = false, desde = 0.41f))
        // El cordero que lleva en brazos.
        add(Zona(MANTO_2, Path().apply { val o = L.p(0.25f, 0.5f); addOval(Rect(o.x - L.r(0.11f), o.y - L.r(0.055f), o.x + L.r(0.11f), o.y + L.r(0.055f))); val q = L.p(0.37f, 0.47f); addOval(Rect(q.x - L.r(0.04f), q.y - L.r(0.035f), q.x + L.r(0.04f), q.y + L.r(0.035f))) }, C.YESO, dibujoDesde = 0.41f, relleno = {
            drawOval(L.tono(lerp(C.YESO, C.PIEDRA, 0.4f)).copy(alpha = 0.6f), L.p(0.15f, 0.5f), Size(L.r(0.2f), L.r(0.05f))); drawCircle(L.tono(C.CARBON), L.r(0.008f), L.p(0.385f, 0.465f))
        }, lineas = { c -> for (k in 0..3) drawArc(c.copy(alpha = c.alpha * 0.6f), 200f, 140f, false, L.p(0.16f + k * 0.045f, 0.47f), Size(L.r(0.04f), L.r(0.04f)), style = Stroke(L.fino * 0.7f)) }))
        addAll(figura(L, 0.76f, 0.44f, 0.96f, 0.3f, C.ULTRAMAR, C.CINABRIO, C.YESO, C.VIGA, -1f, listOf(Offset(0.64f, 0.63f)), a, rodillas = true))
        // El niño, en su paja, con su nimbo.
        add(Zona(DORADO, circulo(L, 0.5f, 0.83f, 0.07f), C.ORO, relleno = { drawCircle(L.tono(lerp(C.ORO, C.YESO, 0.4f)), L.r(0.055f), L.p(0.5f, 0.83f), style = Stroke(L.r(0.01f))) }))
        add(Zona(MANTO_2, poligono(L.p(0.38f, 0.88f), L.p(0.66f, 0.85f), L.p(0.7f, 0.94f), L.p(0.36f, 0.96f)), lerp(C.OCRE, C.YESO, 0.2f), relleno = {
            for (k in 0..10) drawLine(L.tono(lerp(C.OCRE, C.VIGA, 0.3f)).copy(alpha = 0.7f), L.p(0.38f + k * 0.03f, 0.95f), L.p(0.36f + k * 0.032f, 0.87f), L.fino * 0.8f)
            drawPath(Path().apply { moveTo(L.p(0.47f, 0.86f)); cubicTo(L.p(0.5f, 0.82f), L.p(0.62f, 0.82f), L.p(0.64f, 0.87f)); cubicTo(L.p(0.62f, 0.92f), L.p(0.5f, 0.92f), L.p(0.47f, 0.86f)); close() }, L.tono(C.YESO))
        }, lineas = { c -> drawPath(trazo(L, 0.47f, 0.86f, 0.5f, 0.82f, 0.62f, 0.82f, 0.64f, 0.87f), c, style = Stroke(L.fino)) }))
        add(Zona(CARNES, circulo(L, 0.5f, 0.84f, 0.034f), C.CARNE, lineas = { c -> for (k in listOf(-0.012f, 0.012f)) drawCircle(c, L.fino * 0.5f, L.p(0.5f + k, 0.836f)) }))
    }

    /** Los santos: tres figuras de pie bajo tres arcos, sobre un fondo de oro: un obispo con su iglesia, un viejo con su libro y un joven con su palma. */
    private fun santos(L: Lienzo, a: Float): List<Zona> = buildList {
        add(Zona(DORADO, poligono(L.p(0f, 0f), L.p(1f, 0f), L.p(1f, 0.84f), L.p(0f, 0.84f)), C.ORO, relleno = {
            drawRect(Brush.horizontalGradient(0f to L.tono(lerp(C.ORO, C.VIGA, 0.2f)).copy(alpha = 0.5f), 0.4f to L.tono(C.YESO).copy(alpha = 0.3f), 1f to Color.Transparent, startX = L.caja.left, endX = L.caja.right), L.caja.topLeft, L.caja.size)
            // El punzonado del oro: una retícula de puntos.
            if (a >= LUCES) { val puntos = ArrayList<Offset>(); for (i in 1..13) for (j in 1..13) if ((i + j) % 2 == 0) puntos += L.p(i / 14f, 0.84f * j / 14f); drawPoints(puntos, PointMode.Points, L.tono(lerp(C.ORO, C.VIGA, 0.4f)).copy(alpha = 0.5f), L.r(0.012f), StrokeCap.Round) }
        }))
        // El suelo de losas y la arquería: tres arcos de medio punto sobre columnillas.
        val arcos = Path().apply {
            addPath(poligono(L.p(0f, 0.84f), L.p(1f, 0.84f), L.p(1f, 1f), L.p(0f, 1f)))
            for (k in 0..2) { val u0 = k / 3f; val u1 = (k + 1) / 3f
                moveTo(L.p(u0, 0f)); lineTo(L.p(u1, 0f)); lineTo(L.p(u1, 0.2f)); lineTo(L.p(u1 - 0.025f, 0.2f)); cubicTo(L.p(u1 - 0.025f, 0.03f), L.p(u0 + 0.025f, 0.03f), L.p(u0 + 0.025f, 0.2f)); lineTo(L.p(u0, 0.2f)); close()
                addPath(poligono(L.p(u0, 0.2f), L.p(u0 + 0.025f, 0.2f), L.p(u0 + 0.025f, 0.84f), L.p(u0, 0.84f))); addPath(poligono(L.p(u1 - 0.025f, 0.2f), L.p(u1, 0.2f), L.p(u1, 0.84f), L.p(u1 - 0.025f, 0.84f))) }
        }
        add(Zona(SUELO, arcos, lerp(C.YESO, C.MURO, 0.45f), relleno = {
            for (k in 0..6) drawLine(L.tono(lerp(C.CINABRIO, C.MURO, 0.4f)), L.p(k / 6f, 0.84f), L.p(0.5f + (k / 6f - 0.5f) * 1.5f, 1f), L.fino)
            drawRect(L.tono(lerp(C.CINABRIO, C.MURO, 0.3f)).copy(alpha = 0.6f), L.p(0f, 0.9f), Size(L.caja.width, L.r(0.02f))); drawRect(L.tono(lerp(C.VERDE, C.MURO, 0.3f)).copy(alpha = 0.6f), L.p(0f, 0.84f), Size(L.caja.width, L.r(0.014f)))
            for (k in 0..3) { val u = (k / 3f).coerceIn(0.012f, 0.988f); drawRect(L.tono(lerp(C.ORO, C.VIGA, 0.3f)), L.p(u - 0.03f, 0.19f), Size(L.r(0.06f), L.r(0.026f))) }
            for (k in 0..2) drawPath(trazo(L, k / 3f + 0.025f, 0.2f, k / 3f + 0.025f, 0.03f, (k + 1) / 3f - 0.025f, 0.03f, (k + 1) / 3f - 0.025f, 0.2f), L.tono(lerp(C.ULTRAMAR, C.YESO, 0.2f)), style = Stroke(L.r(0.014f)))
        }, lineas = { c -> for (k in 0..2) drawPath(trazo(L, k / 3f + 0.025f, 0.2f, k / 3f + 0.025f, 0.03f, (k + 1) / 3f - 0.025f, 0.03f, (k + 1) / 3f - 0.025f, 0.2f), c, style = Stroke(L.fino)) }))
        addAll(figura(L, 0.167f, 0.3f, 0.93f, 0.23f, C.CINABRIO, C.YESO, null, lerp(C.PIEDRA, C.YESO, 0.4f), 0.6f, listOf(Offset(0.2f, 0.56f)), a, uTunica = MANTO_2, uManto = MANTO_2, barba = true))
        addAll(figura(L, 0.5f, 0.27f, 0.93f, 0.24f, C.ULTRAMAR, lerp(C.OCRE, C.YESO, 0.2f), null, lerp(C.VIGA, C.MADERA, 0.3f), 0f, listOf(Offset(0.44f, 0.55f), Offset(0.56f, 0.55f)), a, uTunica = MANTO_1, uManto = MANTO_1))
        addAll(figura(L, 0.833f, 0.3f, 0.93f, 0.23f, C.VERDE, lerp(C.CINABRIO, C.YESO, 0.3f), null, lerp(C.OCRE, C.VIGA, 0.4f), -0.6f, listOf(Offset(0.8f, 0.56f)), a, uTunica = MANTO_2, uManto = MANTO_2))
        // Lo que lleva cada uno: un libro, la maqueta de una iglesia y una palma. El del centro lleva mitra.
        add(Zona(CARNES, junta(poligono(L.p(0.17f, 0.5f), L.p(0.26f, 0.5f), L.p(0.26f, 0.62f), L.p(0.17f, 0.62f)), poligono(L.p(0.455f, 0.5f), L.p(0.545f, 0.5f), L.p(0.545f, 0.56f), L.p(0.455f, 0.56f)), poligono(L.p(0.44f, 0.5f), L.p(0.56f, 0.5f), L.p(0.5f, 0.44f)), poligono(L.p(0.46f, 0.25f), L.p(0.5f, 0.14f), L.p(0.54f, 0.25f))), lerp(C.YESO, C.MURO, 0.2f), relleno = {
            drawPath(poligono(L.p(0.17f, 0.5f), L.p(0.26f, 0.5f), L.p(0.26f, 0.62f), L.p(0.17f, 0.62f)), L.tono(lerp(C.VIGA, C.CINABRIO, 0.45f))); drawLine(L.tono(C.ORO), L.p(0.185f, 0.52f), L.p(0.185f, 0.6f), L.fino)
            drawPath(poligono(L.p(0.44f, 0.5f), L.p(0.56f, 0.5f), L.p(0.5f, 0.44f)), L.tono(C.TEJA)); drawRect(L.tono(lerp(C.CARBON, C.VIGA, 0.3f)), L.p(0.49f, 0.52f), Size(L.r(0.02f), L.r(0.04f)))
            drawLine(L.tono(C.ORO), L.p(0.5f, 0.15f), L.p(0.5f, 0.25f), L.fino); drawLine(L.tono(C.ORO), L.p(0.465f, 0.245f), L.p(0.535f, 0.245f), L.fino)
        }, lineas = { c -> drawPath(poligono(L.p(0.44f, 0.5f), L.p(0.56f, 0.5f), L.p(0.5f, 0.44f)), c, style = Stroke(L.fino)) }))
        add(Zona(CARNES, Path().apply { moveTo(L.p(0.8f, 0.6f)); cubicTo(L.p(0.9f, 0.5f), L.p(0.92f, 0.4f), L.p(0.89f, 0.3f)); cubicTo(L.p(0.95f, 0.4f), L.p(0.93f, 0.52f), L.p(0.82f, 0.62f)); close() }, lerp(C.VERDE, C.ORO, 0.5f), lineas = { c -> drawPath(trazo(L, 0.81f, 0.61f, 0.9f, 0.5f, 0.92f, 0.4f, 0.89f, 0.3f), c, style = Stroke(L.fino)) }))
    }

    /**
     * El marco de la tabla, que es de una pieza con ella y se enyesa y se dora a la vez: de madera al principio, blanco
     * de yeso con el dibujo, rojo de bol cuando falta poco para el oro y dorado desde entonces.
     */
    private fun DrawScope.marco(caja: Rect, ancho: Float, avance: Float, tono: (Color) -> Color) {
        val color = when { avance >= DORADO -> C.ORO; avance >= DORADO - 0.07f -> lerp(C.CINABRIO, C.VIGA, 0.25f); avance >= 0.03f -> lerp(C.YESO, C.MURO, 0.2f); else -> lerp(C.MADERA, C.MURO, 0.2f) }
        val fuera = Rect(caja.left - ancho, caja.top - ancho, caja.right + ancho, caja.bottom + ancho)
        val aro = Path().apply { addRect(fuera); addRect(caja); fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd }
        drawPath(aro, tono(color))
        // La moldura: el filo de luz arriba y a la izquierda, la sombra abajo y a la derecha, y los ingletes.
        drawLine(tono(lerp(color, Color.White, 0.4f)), fuera.topLeft, fuera.topRight, ancho * 0.3f); drawLine(tono(lerp(color, Color.White, 0.3f)), fuera.topLeft, fuera.bottomLeft, ancho * 0.26f)
        drawLine(tono(lerp(color, C.CARBON, 0.4f)), fuera.bottomLeft, fuera.bottomRight, ancho * 0.3f); drawLine(tono(lerp(color, C.CARBON, 0.3f)), fuera.topRight, fuera.bottomRight, ancho * 0.26f)
        drawRect(tono(lerp(color, C.CARBON, 0.35f)).copy(alpha = 0.7f), Offset(caja.left - ancho * 0.36f, caja.top - ancho * 0.36f), Size(caja.width + ancho * 0.72f, caja.height + ancho * 0.72f), style = Stroke(ancho * 0.16f))
        for ((p, q) in listOf(fuera.topLeft to caja.topLeft, fuera.topRight to caja.topRight, fuera.bottomLeft to caja.bottomLeft, fuera.bottomRight to caja.bottomRight)) drawLine(tono(lerp(color, C.CARBON, 0.4f)).copy(alpha = 0.6f), p, q, 0.4f)
        drawRect(tono(C.CARBON).copy(alpha = 0.45f), caja.topLeft, caja.size, style = Stroke(0.5f))
    }

    /** La tabla grande del maestro, en su [caja], con su marco de [marco] de ancho. [tono]: la luz del sitio. */
    fun DrawScope.tablaGrande(caja: Rect, motivo: Motivo, avance: Float, marco: Float, tono: (Color) -> Color) {
        val L = Lienzo(caja, tono)
        pintar(L, when (motivo) { Motivo.ENCUENTRO -> encuentro(L, avance); Motivo.PASTORES -> pastores(L, avance); Motivo.SANTOS -> santos(L, avance) }, avance)
        marco(caja, marco, avance, tono)
    }

    /** Un santo solo sobre fondo de oro: es lo que llevan las tablas que esperan su turno apoyadas en la pared. [cual] escoge sus colores. */
    fun DrawScope.santo(caja: Rect, cual: Int, avance: Float, tono: (Color) -> Color) {
        val L = Lienzo(caja, tono); val manto = listOf(C.CINABRIO, C.ULTRAMAR, C.VERDE)[Math.floorMod(cual, 3)]; val tunica = listOf(C.VERDE, lerp(C.OCRE, C.YESO, 0.2f), C.CINABRIO)[Math.floorMod(cual, 3)]
        pintar(L, buildList {
            add(Zona(DORADO, poligono(L.p(0f, 0f), L.p(1f, 0f), L.p(1f, 0.86f), L.p(0f, 0.86f)), C.ORO, relleno = { drawRect(Brush.verticalGradient(0f to L.tono(C.YESO).copy(alpha = 0.3f), 1f to Color.Transparent, startY = L.caja.top, endY = L.caja.bottom), L.caja.topLeft, L.caja.size) }))
            add(Zona(SUELO, poligono(L.p(0f, 0.86f), L.p(1f, 0.86f), L.p(1f, 1f), L.p(0f, 1f)), lerp(C.VERDE, C.CARBON, 0.2f)))
            addAll(figura(L, 0.5f, 0.3f, 0.95f, 0.52f, manto, tunica, if (cual % 2 == 0) C.YESO else null, lerp(C.VIGA, C.MADERA, 0.2f), if (cual % 2 == 0) 0.6f else -0.6f, listOf(Offset(0.5f, 0.58f)), avance))
        }, avance)
    }

    /**
     * La primera tabla de Iacopo: una Virgen pequeña, de medio cuerpo, con el niño en brazos, delante de un paño verde y
     * de dos tiras de paisaje. [firmada]: con una hojita dorada abajo, que es su marca (sin letras).
     */
    fun DrawScope.virgen(caja: Rect, avance: Float, firmada: Boolean, marco: Float, tono: (Color) -> Color) {
        val L = Lienzo(caja, tono); val a = avance; val luces = a >= LUCES
        val manto = Path().apply { moveTo(L.p(0.5f, 0.08f)); cubicTo(L.p(0.72f, 0.08f), L.p(0.74f, 0.32f), L.p(0.76f, 0.44f)); cubicTo(L.p(0.96f, 0.56f), L.p(1f, 0.8f), L.p(1f, 1f)); lineTo(L.p(0f, 1f)); cubicTo(L.p(0f, 0.8f), L.p(0.04f, 0.56f), L.p(0.24f, 0.44f)); cubicTo(L.p(0.26f, 0.32f), L.p(0.28f, 0.08f), L.p(0.5f, 0.08f)); close() }
        val pano = Path().apply { moveTo(L.p(0.54f, 0.68f)); cubicTo(L.p(0.6f, 0.56f), L.p(0.86f, 0.58f), L.p(0.88f, 0.72f)); cubicTo(L.p(0.9f, 0.88f), L.p(0.72f, 0.98f), L.p(0.5f, 0.93f)); close() }
        pintar(L, listOf(
            // El paño de honor y, a los lados, dos tiras de cielo con su colina y su ciprés.
            Zona(FONDO, poligono(L.p(0f, 0f), L.p(1f, 0f), L.p(1f, 1f), L.p(0f, 1f)), lerp(C.ULTRAMAR, C.YESO, 0.5f), relleno = {
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to L.tono(C.YESO).copy(alpha = 0.8f), startY = L.caja.top, endY = L.p(0f, 0.56f).y), L.caja.topLeft, Size(L.caja.width, L.caja.height * 0.56f))
                drawPath(Path().apply { moveTo(L.p(0f, 1f)); lineTo(L.p(0f, 0.54f)); cubicTo(L.p(0.1f, 0.5f), L.p(0.14f, 0.56f), L.p(0.2f, 0.55f)); lineTo(L.p(0.8f, 0.55f)); cubicTo(L.p(0.88f, 0.5f), L.p(0.94f, 0.56f), L.p(1f, 0.52f)); lineTo(L.p(1f, 1f)); close() }, L.tono(lerp(C.VERDE, C.OCRE, 0.3f)))
                for (u in listOf(0.05f, 0.1f, 0.92f)) drawPath(poligono(L.p(u - 0.02f, 0.56f), L.p(u, 0.4f), L.p(u + 0.02f, 0.56f)), L.tono(lerp(C.VERDE, C.CARBON, 0.55f)))
                drawRect(L.tono(lerp(C.VERDE, C.CARBON, 0.3f)), L.p(0.15f, 0f), Size(L.r(0.7f), L.caja.height))
                for (k in 0..5) { drawLine(L.tono(lerp(C.VERDE, C.ORO, 0.45f)).copy(alpha = 0.6f), L.p(0.15f + k * 0.14f, 0f), L.p(0.15f + k * 0.14f, 1f), L.fino); for (j in 0..6) drawCircle(L.tono(lerp(C.VERDE, C.ORO, 0.55f)).copy(alpha = 0.6f), L.fino * 0.9f, L.p(0.22f + k * 0.14f, 0.06f + j * 0.14f)) }
                drawRect(L.tono(C.ORO), L.p(0.15f, 0f), Size(L.r(0.7f), L.r(0.02f)))
            }, lineas = { c -> drawLine(c, L.p(0.15f, 0f), L.p(0.15f, 0.5f), L.fino); drawLine(c, L.p(0.85f, 0f), L.p(0.85f, 0.5f), L.fino) }),
            Zona(DORADO, junta(circulo(L, 0.5f, 0.27f, 0.22f), circulo(L, 0.7f, 0.64f, 0.12f)), C.ORO, relleno = {
                for ((u, v, r) in listOf(Triple(0.5f, 0.27f, 0.22f), Triple(0.7f, 0.64f, 0.12f))) { drawCircle(L.tono(lerp(C.ORO, C.YESO, 0.4f)), L.r(r) * 0.84f, L.p(u, v), style = Stroke(L.r(r) * 0.1f)); if (luces) for (k in 0 until 12) { val t = 6.2832f * k / 12f; drawCircle(L.tono(lerp(C.ORO, C.VIGA, 0.45f)), L.r(r) * 0.06f, L.p(u, v) + Offset(cos(t), sin(t)) * (L.r(r) * 0.62f)) } }
            }),
            // El manto azul, de ultramar, que le cubre la cabeza y cae a los dos lados; por dentro, el forro.
            Zona(MANTO_1, manto, C.ULTRAMAR, relleno = {
                drawRect(Brush.horizontalGradient(0f to L.tono(lerp(C.ULTRAMAR, C.YESO, 0.36f)).copy(alpha = 0.8f), 0.4f to Color.Transparent, 1f to L.tono(lerp(C.ULTRAMAR, C.CARBON, 0.5f)).copy(alpha = 0.7f), startX = L.caja.left, endX = L.caja.right), L.caja.topLeft, L.caja.size)
                if (luces) drawPath(trazo(L, 0.5f, 0.09f, 0.72f, 0.08f, 0.74f, 0.32f, 0.76f, 0.44f, 0.96f, 0.56f, 1f, 0.8f, 1f, 1f), L.tono(C.ORO), style = Stroke(L.r(0.016f)))
                if (luces) drawCircle(L.tono(C.ORO), L.r(0.02f), L.p(0.3f, 0.5f))
            }, lineas = { c ->
                for (k in listOf(0.1f, 0.2f, 0.3f)) drawPath(trazo(L, 0.26f, 0.46f, k + 0.06f, 0.6f, k, 0.8f, k - 0.03f, 1f), c, style = Stroke(L.fino, cap = StrokeCap.Round))
                for (k in listOf(0.86f, 0.94f)) drawPath(trazo(L, 0.76f, 0.46f, k - 0.04f, 0.6f, k, 0.8f, k + 0.02f, 1f), c, style = Stroke(L.fino, cap = StrokeCap.Round))
                drawPath(trazo(L, 0.34f, 0.14f, 0.28f, 0.3f, 0.3f, 0.4f, 0.24f, 0.46f), c, style = Stroke(L.fino))
            }),
            // El vestido rojo, que asoma en el pecho, con su ribete.
            Zona(MANTO_2, poligono(L.p(0.36f, 0.45f), L.p(0.62f, 0.45f), L.p(0.6f, 0.9f), L.p(0.32f, 0.9f)), C.CINABRIO, relleno = { drawLine(L.tono(C.ORO), L.p(0.38f, 0.465f), L.p(0.6f, 0.465f), L.r(0.02f)) },
                lineas = { c -> for (k in listOf(0.42f, 0.5f)) drawLine(c, L.p(k, 0.5f), L.p(k - 0.03f, 0.9f), L.fino) }),
            // El niño, envuelto en su paño blanco.
            Zona(MANTO_2, pano, lerp(C.YESO, C.OCRE, 0.2f), lineas = { c -> for (k in listOf(0.7f, 0.8f, 0.9f)) drawPath(trazo(L, 0.58f, k - 0.02f, 0.66f, k + 0.02f, 0.76f, k + 0.02f, 0.86f, k - 0.04f), c, style = Stroke(L.fino)) }),
            // Las carnes, lo último: su cara, inclinada hacia el niño; el cuello; la mano que lo sostiene; y la cabeza y la mano del niño.
            Zona(CARNES, junta(Path().apply { val o = L.p(0.5f, 0.3f); addOval(Rect(o.x - L.r(0.115f), o.y - L.r(0.15f), o.x + L.r(0.115f), o.y + L.r(0.15f))) }, poligono(L.p(0.44f, 0.4f), L.p(0.56f, 0.4f), L.p(0.58f, 0.47f), L.p(0.4f, 0.47f)), circulo(L, 0.7f, 0.65f, 0.072f),
                Path().apply { val o = L.p(0.56f, 0.85f); addOval(Rect(o.x - L.r(0.1f), o.y - L.r(0.045f), o.x + L.r(0.1f), o.y + L.r(0.045f))) }, circulo(L, 0.62f, 0.74f, 0.03f)), C.CARNE, relleno = {
                val o = L.p(0.5f, 0.3f); val rr = L.r(0.115f)
                drawOval(L.tono(lerp(C.CARNE, C.VIGA, 0.34f)).copy(alpha = 0.5f), Offset(o.x - rr, o.y - rr * 1.2f), Size(rr * 0.8f, rr * 2.4f))
                drawCircle(L.tono(lerp(C.CARNE, C.CINABRIO, 0.4f)).copy(alpha = 0.5f), rr * 0.26f, o + Offset(rr * 0.4f, rr * 0.4f)); drawCircle(L.tono(lerp(C.CARNE, C.CINABRIO, 0.4f)).copy(alpha = 0.5f), L.r(0.02f), L.p(0.67f, 0.67f))
                // La toca blanca bajo el manto, y el pelo rubio del niño.
                drawPath(Path().apply { moveTo(L.p(0.385f, 0.32f)); cubicTo(L.p(0.39f, 0.14f), L.p(0.61f, 0.14f), L.p(0.615f, 0.32f)); cubicTo(L.p(0.58f, 0.2f), L.p(0.42f, 0.2f), L.p(0.385f, 0.32f)); close() }, L.tono(C.YESO))
                drawArc(L.tono(lerp(C.OCRE, C.ORO, 0.5f)), 190f, 160f, true, L.p(0.7f, 0.65f) - Offset(L.r(0.076f), L.r(0.08f)), Size(L.r(0.152f), L.r(0.13f)))
                if (luces) drawCircle(L.tono(C.YESO).copy(alpha = 0.6f), rr * 0.16f, o + Offset(-rr * 0.1f, -rr * 0.4f))
            }, lineas = { c ->
                val o = L.p(0.52f, 0.3f); val rr = L.r(0.115f); val g = (rr * 0.12f).coerceAtLeast(0.35f)
                for (k in listOf(-0.36f, 0.4f)) { drawLine(c, o + Offset(rr * (k - 0.2f), -rr * 0.2f), o + Offset(rr * (k + 0.2f), -rr * 0.24f), g * 0.7f); drawArc(c, 20f, 140f, false, o + Offset(rr * (k - 0.18f), -rr * 0.1f), Size(rr * 0.36f, rr * 0.2f), style = Stroke(g * 0.8f)) }
                drawLine(c, o + Offset(rr * 0.06f, -rr * 0.04f), o + Offset(rr * 0.14f, rr * 0.4f), g * 0.7f); drawLine(c, o + Offset(-rr * 0.1f, rr * 0.7f), o + Offset(rr * 0.28f, rr * 0.7f), g)
                val n = L.p(0.69f, 0.65f); for (k in listOf(-0.03f, 0.02f)) drawCircle(c, g * 0.7f, n + Offset(L.r(k), -L.r(0.008f))); drawLine(c, n + Offset(-L.r(0.02f), L.r(0.03f)), n + Offset(L.r(0.012f), L.r(0.03f)), g * 0.8f)
                for (k in listOf(-0.02f, 0f, 0.02f)) drawLine(c, L.p(0.5f, 0.85f + k), L.p(0.63f, 0.84f + k), g * 0.5f)
            }),
        ), avance)
        if (firmada) drawPath(Path().apply { val o = L.p(0.07f, 0.95f); moveTo(o.x, o.y); cubicTo(o.x + L.r(0.03f), o.y - L.r(0.05f), o.x + L.r(0.07f), o.y - L.r(0.05f), o.x + L.r(0.09f), o.y); cubicTo(o.x + L.r(0.06f), o.y + L.r(0.02f), o.x + L.r(0.03f), o.y + L.r(0.02f), o.x, o.y); close() }, tono(lerp(C.ORO, C.YESO, 0.3f)))
        marco(caja, marco, if (firmada) 1f else avance, tono)
    }

    // ---------------------------------------------------------------- Los cartones

    /**
     * Un cartón: una hoja grande de papel con una figura dibujada a carbón y agujereada siguiendo las líneas (se ven
     * los puntitos). [cual] escoge el dibujo: 0 a 2, los de la capilla; 3 en adelante, los nuevos.
     */
    fun DrawScope.carton(caja: Rect, cual: Int, tono: (Color) -> Color) {
        val L = Lienzo(caja, tono); val linea = tono(lerp(C.CARBON, C.VIGA, 0.35f)).copy(alpha = 0.75f)
        drawRect(tono(C.PAPEL), caja.topLeft, caja.size)
        // Las hojas pegadas de que está hecho, y una esquina doblada.
        drawLine(tono(lerp(C.PAPEL, C.VIGA, 0.3f)).copy(alpha = 0.5f), L.p(0f, 0.5f), L.p(1f, 0.5f), 0.4f); drawLine(tono(lerp(C.PAPEL, C.VIGA, 0.3f)).copy(alpha = 0.5f), L.p(0.5f, 0f), L.p(0.5f, 1f), 0.4f)
        drawPath(poligono(L.p(1f, 0.9f), L.p(0.86f, 1f), L.p(1f, 1f)), tono(lerp(C.PAPEL, C.VIGA, 0.35f)))
        val zonas: List<Zona> = when (Math.floorMod(cual, 5)) {
            0 -> figura(L, 0.5f, 0.2f, 0.97f, 0.5f, C.PAPEL, C.PAPEL, C.PAPEL, C.PAPEL, 0.6f, listOf(Offset(0.5f, 0.56f)), 0f, nimbo = false)
            1 -> figura(L, 0.46f, 0.34f, 0.97f, 0.44f, C.PAPEL, C.PAPEL, C.PAPEL, C.PAPEL, 1f, listOf(Offset(0.7f, 0.56f)), 0f, nimbo = false, rodillas = true)
            3 -> figura(L, 0.38f, 0.22f, 0.97f, 0.42f, C.PAPEL, C.PAPEL, null, C.PAPEL, -0.6f, listOf(Offset(0.3f, 0.56f)), 0f, nimbo = false)
            else -> emptyList()
        }
        // Un cartón es todo dibujo: las zonas se quedan en su línea.
        clipPath(Path().apply { addRect(caja) }) { for (z in zonas) { clipPath(z.forma) { z.lineas?.invoke(this, linea.copy(alpha = 0.6f)) }; drawPath(z.forma, linea, style = Stroke(L.fino)) } }
        when (Math.floorMod(cual, 5)) {
            2 -> { // Un brazo con su mano y un estudio de pliegues.
                drawPath(Path().apply { moveTo(L.p(0.14f, 0.2f)); cubicTo(L.p(0.4f, 0.16f), L.p(0.6f, 0.3f), L.p(0.8f, 0.26f)); lineTo(L.p(0.9f, 0.22f)); moveTo(L.p(0.8f, 0.26f)); lineTo(L.p(0.92f, 0.3f)); moveTo(L.p(0.14f, 0.32f)); cubicTo(L.p(0.4f, 0.3f), L.p(0.6f, 0.4f), L.p(0.8f, 0.34f)) }, linea, style = Stroke(L.fino, cap = StrokeCap.Round))
                for (k in 0..4) drawPath(Path().apply { moveTo(L.p(0.2f + k * 0.14f, 0.5f)); cubicTo(L.p(0.16f + k * 0.14f, 0.66f), L.p(0.28f + k * 0.14f, 0.8f), L.p(0.2f + k * 0.15f, 0.94f)) }, linea, style = Stroke(L.fino * 0.8f, cap = StrokeCap.Round)) }
            3 -> drawPath(Path().apply { moveTo(L.p(0.7f, 0.96f)); lineTo(L.p(0.7f, 0.4f)); cubicTo(L.p(0.7f, 0.26f), L.p(0.96f, 0.26f), L.p(0.96f, 0.4f)); lineTo(L.p(0.96f, 0.96f)) }, linea, style = Stroke(L.fino))
            4 -> { // Una arquitectura en perspectiva, con sus líneas de fuga.
                for (k in 0..4) drawLine(linea.copy(alpha = 0.4f), L.p(0.5f, 0.42f), L.p(k / 4f, 1f), L.fino * 0.6f)
                drawPath(Path().apply { moveTo(L.p(0.2f, 0.9f)); lineTo(L.p(0.2f, 0.36f)); cubicTo(L.p(0.2f, 0.1f), L.p(0.8f, 0.1f), L.p(0.8f, 0.36f)); lineTo(L.p(0.8f, 0.9f)); moveTo(L.p(0.34f, 0.7f)); lineTo(L.p(0.34f, 0.44f)); cubicTo(L.p(0.34f, 0.3f), L.p(0.66f, 0.3f), L.p(0.66f, 0.44f)); lineTo(L.p(0.66f, 0.7f)) }, linea, style = Stroke(L.fino)) }
            else -> Unit
        }
        // Los agujeros del estarcido, en lote, y el polvo de carbón que deja la muñequilla.
        val az = Azar(Semilla.de("renacimiento", "carton", cual)); val puntos = ArrayList<Offset>()
        repeat(34) { puntos += L.p(0.1f + 0.8f * (az.entero(1000) / 1000f), 0.1f + 0.84f * (az.entero(1000) / 1000f)) }
        drawPoints(puntos, PointMode.Points, tono(C.CARBON).copy(alpha = 0.22f), 0.5f, StrokeCap.Round)
    }
}
