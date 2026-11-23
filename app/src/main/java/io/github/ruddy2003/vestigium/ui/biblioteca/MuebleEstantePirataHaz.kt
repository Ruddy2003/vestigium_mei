package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.abs

/**
 * El haz de luz que entra por la ventana de la aleta en los Sueños del estante de Pirata ([MuebleEstantePirata]): la luna en
 * oscuro, que cruza en diagonal el mamparo y las repisas de arriba, o el sol bajo del ocaso en claro, que cae por el costado
 * sobre la cornisa, el montante de la izquierda y el primer casillero. No vela: pinta lo que toca. En el aire deja apenas un
 * rastro tenue y de borde blando; donde cae, un paño con borde decidido (mezcla de pantalla: aclara y tiñe sin tapar), y cada
 * superficie lo recibe en su sitio: el plano del frente del mueble (la cornisa, las tablas, los montantes y los cantos de
 * arriba de lo guardado) lo tiene corrido [FRENTE] respecto del fondo (el mamparo y el fondo de los casilleros), así el paño
 * se quiebra en cada escalón. En anchos: de dónde sale ([bx], [by]), su dirección ([ex], [ey], unitaria), su medio ancho
 * ([medio]) y hasta dónde llega ([largo]); su color y su fuerza.
 */
internal class HazPirata(
    val bx: Float, val by: Float, val ex: Float, val ey: Float, val medio: Float, val largo: Float,
    val color: Color, val fuerza: Float,
) {
    /** Cuánto le toca a ([x], [y]) del plano corrido [corrido] a la derecha: de 1 junto a la ventana a 0.25 al final; 0 fuera. */
    fun en(x: Float, y: Float, corrido: Float = 0f): Float {
        val px = x - corrido - bx; val py = y - by
        val l = px * ex + py * ey
        if (l < 0f || l > largo) return 0f
        if (abs(px * ey - py * ex) > medio) return 0f
        return 1f - 0.75f * l / largo
    }

    /** El haz como polígono en un lienzo de ancho [w], corrido [corrido] a la derecha, con su ancho por [ancho]. */
    fun forma(w: Float, corrido: Float, ancho: Float = 1f): Path {
        val nx = -ey * medio * ancho; val ny = ex * medio * ancho
        val ax = bx + corrido; val bx2 = ax + ex * largo; val by2 = by + ey * largo
        return poligonoPirata((ax + nx) * w, (by + ny) * w, (bx2 + nx) * w, (by2 + ny) * w, (bx2 - nx) * w, (by2 - ny) * w, (ax - nx) * w, (by - ny) * w)
    }

    /** La luz a lo largo del haz: fuerte junto a la ventana y apagándose hacia el final ([alfa] en el arranque). */
    fun pincel(w: Float, corrido: Float, alfa: Float): Brush = Brush.linearGradient(
        listOf(color.copy(alpha = alfa), color.copy(alpha = alfa * 0.25f)),
        Offset((bx + corrido) * w, by * w), Offset((bx + corrido + ex * largo) * w, (by + ey * largo) * w),
    )

    companion object {
        /** Cuánto se corre el plano del frente del mueble respecto del fondo, en anchos (la luz entra hacia la derecha). */
        const val FRENTE = -0.035f
    }
}

/** El haz de los Sueños, o ninguno: la luna en oscuro, el sol del ocaso en claro. */
internal fun hazPirata(a: AirePirata): HazPirata? = when {
    a.oscuro && a.noche -> HazPirata(0.1f, 0.2f, 0.62f, 0.785f, 0.115f, 1.5f, Color(0xFFBCCDEA), 0.46f)
    !a.oscuro && a.noche -> HazPirata(-0.06f, 0.36f, 0.5f, 0.866f, 0.055f, 0.78f, PaletaPirata.OCASO, 0.46f)
    else -> null
}

/** El paño del haz [h] sobre lo que hay dentro de [donde], en el plano corrido [corrido]: borde decidido, sin velar. */
internal fun DrawScope.panoHazPirata(h: HazPirata, donde: Path, corrido: Float, fuerza: Float = 1f) {
    val w = size.width
    clipPath(donde) { drawPath(h.forma(w, corrido), h.pincel(w, corrido, h.fuerza * fuerza), blendMode = BlendMode.Screen) }
}

/** El rastro del haz en el aire, entre [y0] y [y1]: muy tenue y de borde blando (tres anchos encimados). */
internal fun DrawScope.rastroHazPirata(h: HazPirata, y0: Float, y1: Float) {
    val w = size.width
    val corrido = HazPirata.FRENTE * 0.5f
    clipRect(0f, y0, w, y1) {
        listOf(1.3f, 0.95f, 0.6f).forEach { an -> drawPath(h.forma(w, corrido, an), h.pincel(w, corrido, 0.035f)) }
    }
}

/**
 * El haz en el cuerpo del mueble, de [ch] a [abajo]: en el fondo de cada casillero (el plano de atrás) y en las tablas y los
 * montantes (el plano del frente, corrido). Lo guardado lo recibe en su canto de arriba ([VolumenEstantePirata]).
 */
internal fun DrawScope.hazEnElCuerpoPirata(h: HazPirata, d: DisposicionEstante, ch: Float, abajo: Float) {
    val w = size.width
    val dT = w * 0.017f; val dI = w * 0.024f
    val fondo = Path()
    d.repisas.forEach { r -> fondo.addRect(Rect(d.izquierda.right + dI, r.top + dT, d.derecha.left - dI, r.bottom)) }
    panoHazPirata(h, fondo, 0f, 0.85f)
    val frente = Path()
    d.tablas.forEach { r -> frente.addRect(r) }
    frente.addRect(Rect(d.izquierda.left, ch, d.izquierda.right, abajo))
    frente.addRect(Rect(d.derecha.left, ch, d.derecha.right, abajo))
    panoHazPirata(h, frente, HazPirata.FRENTE)
}
