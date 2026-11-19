package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.sin

/**
 * Las piezas chicas del libro del día de Espacio ([MuebleTapaEspacio]): la cinta americana de la esquina, la cinta de marcar
 * que flota, la goma que la cierra, el lápiz con su cordón en espiral, la lámpara de lectura y el piloto de guardia.
 */
internal object PiezasTapaEspacio {
    private val P = PaletaEstanteEspacio
    /** La cinta americana gris. */
    private val CINTA_GRIS = Color(0xFFB4B8BE)
    /** El trazo que se rehace en cada pieza (se dibuja de una en una, en el hilo de la interfaz). */
    private val trazo = Path()

    /** La cinta americana gris con que se arregló la esquina gastada de abajo, sobre el lomo: con su trama y su brillo. */
    fun DrawScope.cintaAmericana(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val h = size.height
        val c = Offset(g.bl + 16f * u, g.bb - 9f * u)
        val l = luz.en(c)
        rotate(-28f, c) {
            val r = Rect(c.x - 22f * u, c.y - 7f * u, c.x + 22f * u, c.y + 7f * u)
            poligonoEnEspacio(trazo, r.left, r.top, r.right, r.top + 1f * u, r.right - 1.5f * u, r.top + 4f * u, r.right + 0.5f * u, r.top + 7f * u,
                r.right - 1f * u, r.bottom, r.left + 1f * u, r.bottom, r.left - 1f * u, r.top + 9f * u, r.left + 1f * u, r.top + 5f * u)
            translate(0.6f * u, 0.9f * u) { drawPath(trazo, a.sombra(0.3f)) }
            drawPath(trazo, a.luz(CINTA_GRIS, l))
            trazo.reset(); var x = r.left + 2f * u
            while (x < r.right - 1f * u) { trazo.moveTo(x, r.top + 1f * u); trazo.lineTo(x, r.bottom - 1f * u); x += maxOf(1f, 2.2f * u) }
            drawPath(trazo, a.sombra(0.08f), style = Stroke(0.5f * u))
            drawRect(a.claro(0.35f), Offset(r.left + 2f * u, r.top + 1.5f * u), Size(r.width - 6f * u, 1.2f * u))
        }
        if (h <= 0f) return
    }

    /** La cinta de marcar, roja, que sale por arriba y flota en una ese (sin peso no cae). Al abrir, se mece. */
    fun DrawScope.cintaMarcar(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, semilla: Int, ab: Float) {
        val u = density; val w = size.width
        val x0 = w * (0.69f + 0.04f * azar(semilla, 5))
        val y0 = g.bt + 2f * u
        val m = ab * 4f * u
        trazo.reset()
        trazo.moveTo(x0, y0)
        trazo.cubicTo(x0 - 9f * u, y0 - 5f * u, x0 + 12f * u + m, y0 - 9f * u, x0 + 5f * u + m, y0 - 15f * u)
        val l = luz.en(Offset(x0, y0))
        translate(1.2f * u, 1.8f * u) { drawPath(trazo, a.sombra(0.3f), style = Stroke(3.4f * u)) }
        drawPath(trazo, a.luz(P.ROJO, l), style = Stroke(3.4f * u))
        translate(-0.8f * u, -0.4f * u) { drawPath(trazo, a.claro(0.3f), style = Stroke(0.7f * u)) }
    }

    /**
     * La goma negra que la mantiene cerrada (sin peso se abriría sola), de arriba abajo cerca del canto: con su brillo, su
     * sombra en la lona y, de noche, el filo de la luz. Al abrir, se suelta hacia la derecha.
     */
    fun DrawScope.goma(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, ab: Float) {
        val u = density
        val suelta = (ab / 0.25f).coerceIn(0f, 1f)
        val x = g.goma + suelta * 16f * u
        val an = 4.6f * u
        val l = luz.en(Offset(x, g.libro.center.y))
        val top = g.bt - 2f * u; val bot = g.bb + g.g + 1.5f * u
        drawRect(a.sombra(0.4f), Offset(x + an * 0.5f, top + 2f * u), Size(1.6f * u, bot - top - 2f * u))
        drawRoundRect(a.luz(P.GOMA, l), Offset(x - an / 2, top), Size(an, bot - top), CornerRadius(1.6f * u))
        drawRect(a.claro(0.3f), Offset(x - an / 2 + 0.8f * u, top + 1f * u), Size(0.8f * u, bot - top - 2f * u))
        if (a.lumbre) drawRect(a.filo(0.6f * l.k), Offset(x + an / 2 - 1f * u, top + 1f * u), Size(1f * u, bot - top - 2f * u))
    }

    /**
     * El lápiz de la bitácora, con su cordón en espiral atado a la goma (en la nave un lápiz suelto se pierde): en blanco,
     * metido en la goma; escrito, fuera, flotando junto a la pared con el cordón estirado.
     */
    fun DrawScope.lapiz(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, usado: Boolean, ab: Float) {
        val u = density; val w = size.width; val h = size.height
        val punta: Offset; val cola: Offset
        if (usado) { cola = Offset(w - 12f * u, h * 0.42f); punta = Offset(w - 19f * u + ab * 3f * u, h * 0.8f) }
        else { cola = Offset(g.goma + 3.5f * u, h * 0.3f); punta = Offset(g.goma + 2f * u, h * 0.66f) }
        val l = luz.en((cola + punta) / 2f)
        // El cordón en espiral, de la goma a la cola del lápiz: el alma y las vueltas (un trazo a rayas, grueso).
        val ancla = Offset(g.goma + 1f * u, g.bb - 4f * u)
        trazo.reset()
        trazo.moveTo(ancla.x, ancla.y)
        trazo.cubicTo(ancla.x + 10f * u, ancla.y + 8f * u, cola.x + 4f * u, cola.y + 40f * u, cola.x, cola.y)
        val cord = a.luz(mezcla(P.GOMA, P.TELA_AZUL, 0.35f), l)
        drawPath(trazo, cord, style = Stroke(0.8f * u))
        drawPath(trazo, cord, style = Stroke(3.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.9f * u, 1.3f * u))))
        // El lápiz: el cuerpo amarillo, la goma de borrar roja y la punta afilada.
        val d = punta - cola
        val largo = d.getDistance().coerceAtLeast(1f)
        val ang = (kotlin.math.atan2(d.y, d.x) * 180f / PI.toFloat())
        val sombra = luz.caida(cola, 2.5f * u)
        rotate(ang, cola) {
            val r = Rect(cola.x, cola.y - 2.2f * u, cola.x + largo - 6f * u, cola.y + 2.2f * u)
            drawRect(a.sombra(0.3f), r.topLeft + sombra, Size(largo, r.height))
            drawRect(a.luz(P.TRIGO, l), r.topLeft, r.size)
            drawRect(a.claro(0.35f), r.topLeft, Size(r.width, 1f * u))
            drawRect(a.sombra(0.2f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(a.luz(P.ROJO, l), r.topLeft, Size(3.4f * u, r.height))
            drawRect(a.luz(P.ACERO, l), Offset(r.left + 3.4f * u, r.top), Size(1.6f * u, r.height))
            poligonoEnEspacio(trazo, r.right, r.top, r.right + 6f * u, cola.y, r.right, r.bottom)
            drawPath(trazo, a.luz(mezcla(P.TRIGO, P.PAPEL, 0.5f), l))
            drawCircle(a.luz(P.GOMA, l), 0.9f * u, Offset(r.right + 5.2f * u, cola.y))
            if (a.lumbre) drawRect(a.filo(0.6f * l.k), Offset(r.left, r.top), Size(r.width, 0.8f * u))
        }
    }

    /**
     * La lámpara de lectura de la litera: el cuello de cisne que baja del mazo de cables, la cabeza de goma con su visera y su
     * vidrio. En oscuro (y sin sueño), encendida.
     */
    fun DrawScope.lampara(a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width
        val boca = luz.lampara
        val encendida = a.oscuro && !a.noche
        val l = LuzEstanteEspacio(if (encendida) 0.9f else if (a.lumbre) 0.3f else 1f, 0.1f)
        val base = Offset(w - 11f * u, 6f * u)
        val codo = Offset(w - 6f * u, 22f * u)
        val cab = boca + Offset(9f * u, -9f * u)
        trazo.reset()
        trazo.moveTo(base.x, base.y); trazo.quadraticTo(codo.x, codo.y, cab.x, cab.y)
        translate(1.4f * u, 1.6f * u) { drawPath(trazo, a.sombra(0.3f), style = Stroke(2.6f * u, cap = StrokeCap.Round)) }
        drawPath(trazo, a.luz(P.ACERO, l), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(trazo, a.luz(mezcla(P.ACERO, P.GOMA, 0.5f), l), style = Stroke(2.4f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * u, 1.3f * u))))
        drawRoundRect(a.luz(P.GOMA, l), Offset(base.x - 4f * u, base.y - 4f * u), Size(8f * u, 7f * u), CornerRadius(1.5f * u))
        // La cabeza, girada hacia abajo a la izquierda.
        rotate(135f, boca) {
            val r = Rect(boca.x - 14f * u, boca.y - 4.2f * u, boca.x, boca.y + 4.2f * u)
            drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(0f, -1.8f * u), r.size, CornerRadius(2.5f * u))
            drawRoundRect(a.luz(P.GOMA, l), r.topLeft, r.size, CornerRadius(2.5f * u))
            drawRect(a.claro(0.3f), Offset(r.left + 2f * u, r.bottom - 1.6f * u), Size(r.width - 4f * u, 0.8f * u))
            drawRect(a.luz(P.NARANJA, l), Offset(r.left + 2.5f * u, r.top), Size(1.6f * u, r.height))
            drawOval(if (encendida) Color(0xFFFFEBC8) else a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.45f), l), Offset(boca.x - 1.8f * u, boca.y - 4.6f * u), Size(3.6f * u, 9.2f * u))
        }
    }

    /**
     * El piloto de guardia, una tira de luz atornillada a la pared a la izquierda: apagado de día; en oscuro, tenue; en los
     * sueños, encendido, con su charco en la pared y en el canto del libro.
     */
    fun DrawScope.piloto(a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density
        val c = luz.guardia
        val r = Rect(c.x - 3.4f * u, c.y - 17f * u, c.x + 3.4f * u, c.y + 17f * u)
        drawRoundRect(a.sombra(0.35f), r.topLeft + Offset(1f * u, 1.4f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(a.luz(P.GOMA, LuzEstanteEspacio(0.45f, 0f)), r.topLeft, r.size, CornerRadius(3f * u))
        val lente = r.deflate(1.6f * u)
        if (a.lumbre) {
            val f = if (a.noche) 1f else 0.45f
            if (a.noche) mancha(c, 30f * u, P.GUARDIA.copy(alpha = 0.5f))
            drawRoundRect(mezcla(P.GUARDIA, Color.White, 0.25f + 0.45f * f), lente.topLeft, lente.size, CornerRadius(2f * u))
            drawRect(Color.White.copy(alpha = 0.5f * f), Offset(lente.left + 0.8f * u, lente.top + 2f * u), Size(1f * u, lente.height - 4f * u))
        } else {
            drawRoundRect(a.luz(mezcla(P.GUARDIA, P.ACOLCHADO, 0.55f), LuzEstanteEspacio(1f, 0f)), lente.topLeft, lente.size, CornerRadius(2f * u))
            drawRect(a.claro(0.5f), Offset(lente.left + 0.8f * u, lente.top + 2f * u), Size(1f * u, lente.height - 4f * u))
        }
        remacheEspacio(Offset(c.x, r.top - 2.5f * u), 1f * u, a.luz(P.ACERO, luz.en(c)), a)
        remacheEspacio(Offset(c.x, r.bottom + 2.5f * u), 1f * u, a.luz(P.ACERO, luz.en(c)), a)
    }
}
