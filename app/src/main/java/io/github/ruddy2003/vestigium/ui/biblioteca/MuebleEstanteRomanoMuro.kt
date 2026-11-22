package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Lo que equivale a [AireRomano.luz] puesto encima de algo ya pintado con sus colores de día: el anochecer o la noche, y el
 * calor de la linterna según [k]. Sirve para apagar por tramos lo que se pinta de una vez (los ladrillos, el suelo), así la
 * noche no queda pareja.
 */
internal fun DrawScope.veloRomano(r: Rect, a: AireRomano, k: Float) {
    if (r.width <= 0f || r.height <= 0f) return
    val kk = k.coerceIn(0f, 1f)
    val p = PaletaRomano
    if (!a.oscuro) {
        if (!a.noche) return
        drawRect(p.ANOCHECER.copy(alpha = (0.38f - 0.22f * kk).coerceIn(0f, 1f)), r.topLeft, r.size)
        if (kk > 0.02f) drawRect(p.LLAMA.copy(alpha = 0.12f * kk), r.topLeft, r.size)
        return
    }
    drawRect(a.hondo.copy(alpha = (0.82f - 0.84f * kk.pow(0.85f)).coerceIn(0f, 1f)), r.topLeft, r.size)
    if (kk > 0.02f) drawRect(p.LLAMA.copy(alpha = 0.3f * kk), r.topLeft, r.size)
}

/**
 * El muro encalado del cuarto del escribiente, la ventana chica de espejuelo arriba a la izquierda y el suelo de ladrillo en
 * espiga del estante de Romano ([MuebleEstanteRomano]).
 */
internal object MuroEstanteRomano {
    private val P = PaletaRomano

    /** La ventana, en anchos del mueble. */
    private const val V0X = 0.075f
    private const val V0Y = 0.07f
    private const val V1X = 0.255f
    private const val V1Y = 0.29f

    /**
     * El muro de cal entero: su cara con pinceladas de brocha, la mancha de sol que deja la ventana (de día), las líneas de
     * almagre gastadas (la de arriba y los paños), dos grietas, el hollín sobre la linterna y el zócalo de almagre abajo.
     */
    fun DrawScope.muro(a: AireRomano, ch: Float, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = a.luz(P.CAL, if (a.lumbre) 0.05f else 1f)
        drawRect(fondo, Offset.Zero, Size(w, h))
        // El sol de la ventana en la cal, de día: una mancha ancha y blanda (el espejuelo no deja rayo, lo difumina).
        if (!a.oscuro && !a.noche) {
            mancha(Offset(w * 0.25f, w * 0.36f), w * 0.5f, P.CAL_CLARA.copy(alpha = 0.85f))
            // Y lo que se aleja de la ventana se enfría un poco.
            drawRect(P.FRIO.copy(alpha = 0.05f), Offset(w * 0.62f, 0f), Size(w * 0.38f, ch))
        }
        // La cal no es lisa: pasadas de brocha más claras y más grises.
        val sup = Rect(0f, 0f, w, ch)
        pinceladas(sup, a.luz(P.CAL_SOMBRA, 0.2f), 77, 34, u, alfa = if (a.oscuro) 0.06f else 0.1f)
        pinceladas(sup, a.claro(1f), 78, 20, u, alfa = if (a.oscuro) 0.025f else 0.14f)
        // Las líneas de almagre pintadas, gastadas: una faja doble arriba y los paños que bajan.
        val pinta = a.luz(P.ALMAGRE, cerca(Offset(w * 0.6f, w * 0.05f))).copy(alpha = 0.62f)
        tramado(0f, w, w * 0.032f, 2.4f * u, pinta, 31)
        tramado(0f, w, w * 0.052f, 0.9f * u, pinta, 32)
        for ((j, fx) in listOf(0.315f, 0.7f).withIndex()) {
            val x = w * fx
            var y = w * 0.052f
            var i = 0
            while (y < ch - w * 0.06f && i < 40) {
                val largo = w * (0.04f + 0.06f * azar(40 + j, i))
                drawLine(pinta, Offset(x, y), Offset(x, minOf(y + largo, ch - w * 0.06f)), 1.2f * u)
                y += largo + w * 0.012f * azar(50 + j, i)
                i++
            }
        }
        // Dos grietas de la cal.
        grieta(Offset(w * 0.47f, w * 0.06f), w * 0.11f, 61, a)
        grieta(Offset(w * 0.93f, w * 0.12f), w * 0.09f, 62, a)
        // El hollín de la linterna en la cal, encima de ella (de día también se ve).
        val lx = w * MuebleEstanteRomano.LAMP_X
        for (j in 0 until 4) {
            val an = w * (0.13f - 0.025f * j)
            drawOval(P.TINTA.copy(alpha = if (a.oscuro) 0.08f else 0.035f), Offset(lx - an / 2 + (j - 1.5f) * 1.5f * u, w * (0.0f + 0.04f * j)), Size(an, w * (0.24f - 0.03f * j)))
        }
        // El zócalo de almagre, abajo, bajo la última tabla, con su filete y su desgaste.
        val z0 = suelo - w * 0.07f
        drawRect(a.luz(P.ALMAGRE, 0.04f), Offset(0f, z0), Size(w, suelo - z0))
        pinceladas(Rect(0f, z0, w, suelo), a.luz(P.ALMAGRE_OSC, 0.04f), 79, 12, u, alfa = 0.25f)
        drawRect(a.luz(P.CAL_CLARA, 0.04f).copy(alpha = 0.5f), Offset(0f, z0 - 1.6f * u), Size(w, 1f * u))
    }

    /** Una línea pintada a mano, que se corta donde la cal se gastó. */
    private fun DrawScope.tramado(x0: Float, x1: Float, y: Float, grosor: Float, color: Color, semilla: Int) {
        var x = x0
        var i = 0
        while (x < x1 && i < 60) {
            val largo = size.width * (0.06f + 0.14f * azar(semilla, i))
            val dy = (azar(semilla, 100 + i) - 0.5f) * grosor * 0.5f
            drawLine(color, Offset(x, y + dy), Offset(minOf(x + largo, x1), y - dy), grosor * (0.7f + 0.4f * azar(semilla, 200 + i)))
            x += largo + size.width * 0.02f * azar(semilla, 300 + i)
            i++
        }
    }

    /** Una grieta de la cal: un trazo quebrado que se adelgaza, con su labio claro. */
    private fun DrawScope.grieta(desde: Offset, largo: Float, semilla: Int, a: AireRomano) {
        val u = density
        val p = Path().apply {
            moveTo(desde.x, desde.y)
            var x = desde.x; var y = desde.y
            for (i in 1..5) {
                x += (azar(semilla, i) - 0.5f) * largo * 0.35f
                y += largo / 5f
                lineTo(x, y)
            }
        }
        drawPath(corridoRomano(p, 0.6f * u, 0.4f * u), a.claro(0.4f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawPath(p, a.frio(0.4f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    /**
     * La ventana chica y alta del cuarto: el muro grueso deja ver su derrame (la cara de arriba en sombra, el alféizar de
     * piedra con luz), el marco de roble y seis paños de espejuelo, la piedra traslúcida que se partía en hojas: lechosa, con
     * vetas tornasoladas y una junta por paño. De día deja pasar la luz difusa; de noche, el azul de fuera y la luna
     * desdibujada detrás; en los Sueños en claro, el anochecer.
     */
    fun DrawScope.ventana(a: AireRomano) {
        val u = density
        val w = size.width
        val o = Rect(w * V0X, w * V0Y, w * V1X, w * V1Y)
        val fondo = w * 0.016f
        val i = Rect(o.left + fondo, o.top + fondo * 1.3f, o.right - fondo * 0.6f, o.bottom - fondo * 0.4f)
        val kM = if (a.lumbre) 0.05f else 1f
        // El arco de descarga de ladrillo encima del vano, que se transparenta bajo la cal.
        val ra = o.width * 0.6f
        val ca = Offset(o.center.x, o.top - w * 0.008f + ra)
        val r1 = ra + w * 0.026f
        val dovelas = Path()
        for (j in 0 until 11) {
            val a0 = Math.toRadians(205.0 + 130.0 * j / 11 + 1.2).toFloat()
            val a1 = Math.toRadians(205.0 + 130.0 * (j + 1) / 11 - 1.2).toFloat()
            dovelas.addPath(poligonoRomano(ca.x + ra * cos(a0), ca.y + ra * sin(a0), ca.x + r1 * cos(a0), ca.y + r1 * sin(a0),
                ca.x + r1 * cos(a1), ca.y + r1 * sin(a1), ca.x + ra * cos(a1), ca.y + ra * sin(a1)))
        }
        drawPath(dovelas, a.luz(P.LADRILLO, kM).copy(alpha = 0.24f))
        drawPath(corridoRomano(dovelas, 0.8f * u, 0.8f * u), a.frio(0.07f))
        // El derrame: arriba en sombra, la izquierda a media luz, la derecha clara y el alféizar.
        drawRect(a.luz(P.CAL_SOMBRA, kM), o.topLeft, o.size)
        drawPath(poligonoRomano(o.left, o.top, o.right, o.top, i.right, i.top, i.left, i.top), a.frio(0.4f))
        drawPath(poligonoRomano(o.left, o.top, i.left, i.top, i.left, i.bottom, o.left, o.bottom), a.frio(0.18f))
        drawPath(poligonoRomano(o.right, o.top, o.right, o.bottom, i.right, i.bottom, i.right, i.top), a.luz(P.CAL_CLARA, kM))
        // Los paños de espejuelo.
        val cols = 3; val filas = 3
        val marco = w * 0.0065f
        val anP = (i.width - marco * (cols + 1)) / cols
        val alP = (i.height - marco * (filas + 1)) / filas
        drawRect(a.luz(mezcla(P.BRONCE, P.TINTA, 0.45f), kM), i.topLeft, i.size)
        for (f in 0 until filas) for (c in 0 until cols) {
            val r = Rect(i.left + marco + c * (anP + marco), i.top + marco + f * (alP + marco), i.left + marco + c * (anP + marco) + anP, i.top + marco + f * (alP + marco) + alP)
            pano(r, f, c, filas, a)
        }
        // El marco de roble con su luz arriba a la izquierda (la de fuera le da por delante) y sus clavijas.
        drawRect(a.claro(0.25f), Offset(i.left, i.top), Size(i.width, 0.8f * u))
        drawRect(a.frio(0.3f), Offset(i.left, i.bottom - 0.8f * u), Size(i.width, 0.8f * u))
        // La luna desdibujada detrás del espejuelo, de noche.
        if (a.oscuro) {
            val luna = Offset(i.left + i.width * 0.7f, i.top + i.height * 0.3f)
            mancha(luna, i.width * (if (a.noche) 0.75f else 0.5f), P.LUNA.copy(alpha = if (a.noche) 0.55f else 0.35f))
        }
        // El alféizar de piedra que sobresale, con su luz arriba y su sombra en la cal.
        val al = Rect(o.left - w * 0.012f, o.bottom - w * 0.006f, o.right + w * 0.012f, o.bottom + w * 0.014f)
        drawRect(a.sombra(0.28f), Offset(al.left + w * 0.01f, al.bottom), Size(al.width, w * 0.012f))
        drawRect(a.luz(P.MORTERO, kM), al.topLeft, al.size)
        drawRect(a.claro(0.5f), al.topLeft, Size(al.width, al.height * 0.3f))
        drawRect(a.frio(0.25f), Offset(al.left, al.bottom - al.height * 0.25f), Size(al.width, al.height * 0.25f))
        // En el alféizar, el vaso de barro de la posca.
        val vx = o.left + o.width * 0.72f
        val vaso = poligonoRomano(vx - w * 0.014f, al.top - w * 0.03f, vx + w * 0.014f, al.top - w * 0.03f, vx + w * 0.01f, al.top, vx - w * 0.01f, al.top)
        drawPath(corridoRomano(vaso, w * 0.008f, 0f), a.sombra(0.3f))
        drawPath(vaso, a.luz(P.BARRO, kM))
        drawRect(a.claro(0.35f), Offset(vx - w * 0.011f, al.top - w * 0.028f), Size(w * 0.005f, w * 0.024f))
        drawRect(a.luz(P.ALMAGRE_OSC, kM), Offset(vx - w * 0.015f, al.top - w * 0.032f), Size(w * 0.03f, w * 0.004f))
    }

    /** Un paño de espejuelo: el color de lo que hay fuera, lechoso, con su junta en diagonal y una veta tornasolada. */
    private fun DrawScope.pano(r: Rect, f: Int, c: Int, filas: Int, a: AireRomano) {
        val u = density
        val s = 7 * f + 3 * c
        val baja = (f + 0.5f) / filas
        val color = when {
            a.oscuro && a.noche -> mezcla(Color(0xFF26346A), Color(0xFF151C40), baja)
            a.oscuro -> mezcla(Color(0xFF2A3148), Color(0xFF181B2A), baja)
            a.noche -> mezcla(P.ANOCHECER, P.CREPUSCULO, baja * 0.9f)
            else -> mezcla(listOf(Color(0xFFE4DFC6), P.ESPEJUELO, Color(0xFFD3DCCF))[Math.floorMod(s * 5 + 1, 3)], Color(0xFFF5F0E2), azar(s, 1) * 0.5f)
        }
        drawRect(color, r.topLeft, r.size)
        // La hoja de piedra no llega entera: una junta en diagonal con la otra hoja, apenas más turbia.
        val jy0 = r.top + r.height * (0.3f + 0.4f * azar(s, 2)); val jy1 = r.top + r.height * (0.2f + 0.5f * azar(s, 3))
        drawPath(poligonoRomano(r.left, jy0, r.right, jy1, r.right, r.bottom, r.left, r.bottom),
            if (a.oscuro) Color.Black.copy(alpha = 0.12f) else P.CAL_SOMBRA.copy(alpha = 0.16f))
        drawLine(if (a.oscuro) Color.Black.copy(alpha = 0.3f) else P.FRIO.copy(alpha = 0.25f), Offset(r.left, jy0), Offset(r.right, jy1), 0.6f * u)
        // La veta tornasolada.
        if (!a.oscuro) {
            val t = if (azar(s, 4) > 0.5f) Color(0xFFD9C8E6) else Color(0xFFCFE3D6)
            drawLine(t.copy(alpha = 0.7f), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.2f), Offset(r.left + r.width * 0.7f, r.top + r.height * 0.45f), 1.1f * u)
        }
    }

    /**
     * El suelo de ladrillo en espiga, del pie del estante al borde: hiladas en zigzag, cada ladrillo con su tono, que crecen
     * un poco hacia delante; la sombra del estante donde empieza y, de noche, el calor de la linterna que apenas llega.
     */
    fun DrawScope.espiga(a: AireRomano, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        if (h - suelo < 1f) return
        clipRect(0f, suelo, w, h) {
            drawRect(P.MORTERO, Offset(0f, suelo), Size(w, h - suelo))
            val claros = Path(); val medios = Path(); val oscuros = Path()
            var y = suelo
            var fila = 0
            while (y < h && fila < 30) {
                val alto = maxOf(2f * u, w * (0.03f + 0.006f * fila))
                val paso = maxOf(2f * u, alto * 1.05f)
                val dx = if (fila % 2 == 0) 0f else paso * 0.5f
                var x = -paso + dx
                var j = 0
                while (x < w + paso && j < 200) {
                    val izq = j % 2 == 0
                    val g = 0.08f * alto
                    val p = if (izq) poligonoRomano(x + g, y + g, x + paso - g, y + alto * 0.5f + g * 0.2f, x + paso - g, y + alto - g, x + g, y + alto * 0.5f - g * 0.2f)
                    else poligonoRomano(x + g, y + alto * 0.5f + g * 0.2f, x + paso - g, y + g, x + paso - g, y + alto * 0.5f - g * 0.2f, x + g, y + alto - g)
                    val t = azar(fila * 131 + j, 9)
                    (if (t < 0.3f) claros else if (t < 0.75f) medios else oscuros).addPath(p)
                    x += paso * 0.5f
                    j++
                }
                y += alto
                fila++
            }
            drawPath(claros, P.LADRILLO_CLARO)
            drawPath(medios, P.LADRILLO)
            drawPath(oscuros, P.LADRILLO_OSC)
            // La luz: de día el sol de la ventana llega al suelo de la izquierda; lo de la derecha, más frío.
            if (!a.oscuro && !a.noche) {
                drawRect(a.claro(0.12f), Offset(0f, suelo), Size(w * 0.45f, h - suelo))
                drawRect(a.frio(0.1f), Offset(w * 0.65f, suelo), Size(w * 0.35f, h - suelo))
            }
            // La noche, por tramos: la linterna apenas llega al suelo de la derecha.
            val tramos = 6
            for (t in 0 until tramos) {
                val x0 = w * t / tramos
                veloRomano(Rect(x0, suelo, x0 + w / tramos + 1f, h), a, cerca(Offset(x0 + w / tramos / 2, suelo)) * 0.8f)
            }
            // La sombra del estante donde empieza el suelo, y el polvo de harina gastado hacia delante.
            drawRect(a.sombra(0.4f), Offset(0f, suelo), Size(w, 3f * u))
            drawRect(a.sombra(0.18f), Offset(0f, suelo + 3f * u), Size(w, 5f * u))
        }
    }
}
